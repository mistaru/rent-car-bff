package kg.founders.core.entity.rental;

import kg.founders.core.enums.DocumentType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, foreignKey = @ForeignKey(name = "fk_doc_booking"))
    private Booking booking;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "stored_file_name", nullable = false, length = 500)
    private String storedFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "document_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_by", nullable = false, length = 100)
    private String uploadedBy; // "client" or admin username

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

}