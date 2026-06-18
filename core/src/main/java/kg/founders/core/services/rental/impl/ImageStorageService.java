package kg.founders.core.services.rental.impl;

import kg.founders.core.config.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final ImageStorageProperties props;

    @PostConstruct
    public void init() throws IOException {
        Path dir = Paths.get(props.getStorageDir());
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            log.info("Created image storage directory: {}", dir);
        }
    }

    /**
     * Сохраняет файл на диск, возвращает уникальное имя файла.
     * Пример: "v15_a3f2c1b8.webp"
     */
    public String store(Long vehicleId, MultipartFile file) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String uniqueName = "v" + vehicleId + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

        Path target = Paths.get(props.getStorageDir()).resolve(uniqueName).toAbsolutePath().normalize();
        Files.createDirectories(target.getParent());
        Files.copy(file.getInputStream(), target);

        log.info("Stored image: {} ({} bytes)", uniqueName, file.getSize());
        return uniqueName;
    }

    /** Публичный URL для отображения в img src */
    public String getPublicUrl(String storageFilename) {
        if (storageFilename == null) return null;
        return props.getBaseUrl() + "/" + storageFilename;
    }

    /** Удалить файл с диска */
    public void delete(String storageFilename) {
        if (storageFilename == null) return;
        try {
            Path path = Paths.get(props.getStorageDir()).resolve(storageFilename);
            Files.deleteIfExists(path);
            log.info("Deleted image file: {}", storageFilename);
        } catch (IOException e) {
            log.warn("Failed to delete image file: {}", storageFilename, e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "jpg";
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toLowerCase() : "jpg";
    }
}

