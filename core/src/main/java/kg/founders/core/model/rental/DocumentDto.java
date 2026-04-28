package kg.founders.core.model.rental;

public record DocumentDto(Long id, String fileName, String contentType, String documentType,
                          Long fileSize, String uploadedBy, String uploadedAt) {}
