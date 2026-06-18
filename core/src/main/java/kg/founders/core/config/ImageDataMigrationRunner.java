package kg.founders.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Одноразовый миграционный скрипт: переносит bytea из колонки "data" на файловую систему.
 * После успешной миграции этот класс можно удалить.
 *
 * Работает напрямую через JdbcTemplate, т.к. entity уже не содержит поле data.
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class ImageDataMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final ImageStorageProperties props;

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли ещё колонка data в таблице
        if (!columnExists("vehicle_images", "data")) {
            log.info("Column 'data' not found in vehicle_images — migration already done.");
            return;
        }

        // Убираем NOT NULL с колонки data, чтобы новые записи не требовали её заполнения
        try {
            jdbcTemplate.execute("ALTER TABLE vehicle_images ALTER COLUMN data DROP NOT NULL");
            log.info("Dropped NOT NULL constraint from column 'data'");
        } catch (Exception e) {
            log.debug("Could not drop NOT NULL from 'data' (may already be nullable): {}", e.getMessage());
        }

        // Проверяем, что колонка storage_filename уже создана Hibernate
        if (!columnExists("vehicle_images", "storage_filename")) {
            log.warn("Column 'storage_filename' not yet created in vehicle_images — skipping migration this run.");
            return;
        }

        // Находим записи, у которых есть data, но нет storage_filename
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT vi.id, vi.vehicle_id, vi.mime_type, vi.data, vi.filename " +
                        "FROM vehicle_images vi " +
                        "WHERE vi.data IS NOT NULL AND (vi.storage_filename IS NULL OR vi.storage_filename = '')"
        );

        if (rows.isEmpty()) {
            log.info("No images to migrate from bytea to disk.");
            return;
        }

        log.info("Migrating {} images from bytea to disk...", rows.size());

        Path storageDir = Paths.get(props.getStorageDir());
        Files.createDirectories(storageDir);

        int success = 0;
        for (Map<String, Object> row : rows) {
            try {
                Long id = ((Number) row.get("id")).longValue();
                Long vehicleId = ((Number) row.get("vehicle_id")).longValue();
                String mimeType = (String) row.get("mime_type");
                byte[] data = (byte[]) row.get("data");
                String originalFilename = (String) row.get("filename");

                if (data == null || data.length == 0) {
                    log.warn("Image id={} has empty data, skipping", id);
                    continue;
                }

                String ext = extensionFromMime(mimeType);
                String storageFilename = "v" + vehicleId + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

                // Записать файл на диск
                Files.write(storageDir.resolve(storageFilename), data);

                // Обновить запись в БД
                jdbcTemplate.update(
                        "UPDATE vehicle_images SET storage_filename = ?, original_filename = ?, file_size = ? WHERE id = ?",
                        storageFilename, originalFilename, (long) data.length, id
                );

                success++;
                log.info("Migrated image id={} → {}", id, storageFilename);
            } catch (Exception e) {
                Long id = row.get("id") != null ? ((Number) row.get("id")).longValue() : -1;
                log.error("Failed to migrate image id={}", id, e);
            }
        }

        log.info("Image migration complete: {}/{} succeeded.", success, rows.size());
    }

    private boolean columnExists(String table, String column) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                    Integer.class, table, column
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private String extensionFromMime(String mime) {
        if (mime == null) return "jpg";
        switch (mime) {
            case "image/png": return "png";
            case "image/webp": return "webp";
            default: return "jpg";
        }
    }
}

