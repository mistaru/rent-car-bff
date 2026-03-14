package kg.founders.bff.controller.rental;

import kg.founders.core.entity.rental.VehicleImage;
import kg.founders.core.model.rental.VehicleImageDto;
import kg.founders.core.services.rental.VehicleImageService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-images")
@RequiredArgsConstructor
public class VehicleImageController {

    private final VehicleImageService imageService    ;

    /** Список мета-данных по авто */
    @ManualPermissionControl
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleImageDto>> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(imageService.getImagesByVehicleId(vehicleId));
    }

    /** Отдать бинарник (используется как <img :src="...">) */
    @ManualPermissionControl
    @GetMapping("/{id}/data")
    public ResponseEntity<byte[]> getData(@PathVariable Long id) {
        VehicleImage image = imageService.getRawById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getMimeType()))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(image.getData());
    }

    /** Загрузить новое фото */
    @ManualPermissionControl
    @PostMapping("/vehicle/{vehicleId}")
    public ResponseEntity<VehicleImageDto> upload(
            @PathVariable Long vehicleId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain
    ) throws IOException {
        return ResponseEntity.ok(imageService.upload(vehicleId, file, isMain));
    }

    /** Сделать главным */
    @ManualPermissionControl
    @PatchMapping("/{id}/set-main")
    public ResponseEntity<Void> setMain(@PathVariable Long id) {
        imageService.setMain(id);
        return ResponseEntity.noContent().build();
    }

    /** Удалить */
    @ManualPermissionControl
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}