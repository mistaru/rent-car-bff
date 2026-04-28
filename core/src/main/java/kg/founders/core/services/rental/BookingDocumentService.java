package kg.founders.core.services.rental;

import kg.founders.core.enums.DocumentType;
import kg.founders.core.model.rental.DocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookingDocumentService {
    @Transactional
    DocumentDto upload(Long bookingId, MultipartFile file,
                       DocumentType docType, String uploadedBy) throws IOException;

    List<DocumentDto> getByBooking(Long bookingId);

    Resource download(Long bookingId, Long documentId) throws IOException;

    String getContentType(Long documentId);

    @Transactional
    void delete(Long bookingId, Long documentId) throws IOException;
}
