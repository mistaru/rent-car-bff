package kg.founders.bff.controller.rental;

import kg.founders.core.enums.DocumentType;
import kg.founders.core.services.rental.BookingDocumentService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings/{bookingId}/documents")
@RequiredArgsConstructor
public class BookingDocumentController {

    private final BookingDocumentService bookingDocumentService;

    @ManualPermissionControl
    @PostMapping
    public ResponseEntity<?> upload(
        @PathVariable Long bookingId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") DocumentType type,
        @RequestParam(value = "uploadedBy", defaultValue = "client") String uploadedBy
    ) throws IOException {
        return ResponseEntity.ok(bookingDocumentService.upload(bookingId, file, type, uploadedBy));
    }

    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<?>> list(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingDocumentService.getByBooking(bookingId));
    }

    @ManualPermissionControl
    @GetMapping("/{docId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long bookingId, @PathVariable Long docId) throws IOException {
        Resource resource = bookingDocumentService.download(bookingId, docId);
        String contentType = bookingDocumentService.getContentType(docId);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
            .body(resource);
    }

    @ManualPermissionControl
    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookingId, @PathVariable Long docId) throws IOException {
        bookingDocumentService.delete(bookingId, docId);
        return ResponseEntity.noContent().build();
    }
}
