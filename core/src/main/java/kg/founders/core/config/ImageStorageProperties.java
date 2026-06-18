package kg.founders.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.images")
@Getter
@Setter
public class ImageStorageProperties {
    /** Директория на диске: /app/images */
    private String storageDir = "/app/images";
    /** Публичный URL-префикс для клиента */
    private String baseUrl = "http://178.105.184.173/images";
}

