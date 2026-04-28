package kg.founders.core.services.rental.impl;


import kg.founders.core.entity.rental.BookingDocument;
import kg.founders.core.enums.DocumentType;
import kg.founders.core.model.rental.DocumentDto;
import kg.founders.core.repo.BookingDocumentRepository;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.services.rental.BookingDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingDocumentServiceImpl implements BookingDocumentService {

    private final BookingDocumentRepository documentRepository;
    private final BookingRepository bookingRepository;

    @Value("${app.documents.upload-dir:uploads/documents}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of(
        "image/jpeg", "image/png", "image/webp", "application/pdf"
    );

    @Transactional
    @Override
    public DocumentDto upload(Long bookingId, MultipartFile file,
                              DocumentType docType, String uploadedBy) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("File exceeds 10MB limit");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());

        var booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        Path dir = Paths.get(uploadDir, String.valueOf(bookingId));
        Files.createDirectories(dir);

        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = dir.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        var doc = BookingDocument.builder()
            .booking(booking)
            .fileName(file.getOriginalFilename())
            .storedFileName(storedName)
            .contentType(file.getContentType())
            .documentType(docType)
            .fileSize(file.getSize())
            .uploadedBy(uploadedBy)
            .build();

        return toDto(documentRepository.save(doc));
    }

    @Override
    public List<DocumentDto> getByBooking(Long bookingId) {
        return documentRepository.findByBookingId(bookingId).stream().map(this::toDto).toList();
    }

    @Override
    public Resource download(Long bookingId, Long documentId) throws IOException {
        var doc = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        if (!doc.getBooking().getId().equals(bookingId))
            throw new IllegalArgumentException("Document does not belong to this booking");

        Path filePath = Paths.get(uploadDir, String.valueOf(bookingId), doc.getStoredFileName());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) throw new IOException("File not found on disk");
        return resource;
    }

    @Override
    public String getContentType(Long documentId) {
        return documentRepository.findById(documentId)
            .map(BookingDocument::getContentType)
            .orElse("application/octet-stream");
    }

    @Transactional
    @Override
    public void delete(Long bookingId, Long documentId) throws IOException {
        var doc = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        if (!doc.getBooking().getId().equals(bookingId))
            throw new IllegalArgumentException("Document does not belong to this booking");

        Path filePath = Paths.get(uploadDir, String.valueOf(bookingId), doc.getStoredFileName());
        Files.deleteIfExists(filePath);
        documentRepository.delete(doc);
    }

    private DocumentDto toDto(BookingDocument d) {
        return new DocumentDto(d.getId(), d.getFileName(), d.getContentType(),
                d.getDocumentType().name(), d.getFileSize(), d.getUploadedBy(),
                d.getUploadedAt() != null ? d.getUploadedAt().toString() : null);
    }
}
