package kg.founders.core.web.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.rental.VehiclePhotos;
import kg.founders.core.model.rental.VehiclePhotosDto;
import kg.founders.core.services.rental.VehiclePhotosService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VehiclePhotosController {

    private final VehiclePhotosService vehiclePhotosService;
    private final RentalMapper rentalMapper;

    /**
     * Gets all photos for a specific vehicle, ordered by sortOrder.
     * Note: The service method is getPhotosByCarId, but we use vehicleId for path consistency.
     */
    @GetMapping("/vehicles/{vehicleId}/photos")
    public ResponseEntity<List<VehiclePhotosDto>> getPhotosForVehicle(@PathVariable Long vehicleId) {
        List<VehiclePhotos> photos = vehiclePhotosService.getPhotosByVehicleId(vehicleId);
        List<VehiclePhotosDto> photoDtos = photos.stream()
                .map(rentalMapper::toVehiclePhotosDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(photoDtos);
    }

    /**
     * Adds a new photo to a specific vehicle.
     */
    @PostMapping("/vehicles/{vehicleId}/photos")
    public ResponseEntity<VehiclePhotosDto> addPhotoToVehicle(@PathVariable Long vehicleId, @RequestBody CreatePhotoRequest request) {
        VehiclePhotos newPhoto = vehiclePhotosService.addPhoto(vehicleId, request.getUrl(), request.getSortOrder());
        return new ResponseEntity<>(rentalMapper.toVehiclePhotosDto(newPhoto), HttpStatus.CREATED);
    }

    /**
     * Retrieves a single photo by its unique ID.
     */
    @GetMapping("/vehicle-photos/{photoId}")
    public ResponseEntity<VehiclePhotosDto> getPhotoById(@PathVariable Long photoId) {
        VehiclePhotos photo = vehiclePhotosService.getPhoto(photoId);
        return ResponseEntity.ok(rentalMapper.toVehiclePhotosDto(photo));
    }

    /**
     * Updates the sort order of a specific photo.
     */
    @PutMapping("/vehicle-photos/{photoId}/sort-order")
    public ResponseEntity<VehiclePhotosDto> updatePhotoSortOrder(@PathVariable Long photoId, @RequestBody UpdateSortOrderRequest request) {
        VehiclePhotos updatedPhoto = vehiclePhotosService.updatePhotoSortOrder(photoId, request.getNewSortOrder());
        return ResponseEntity.ok(rentalMapper.toVehiclePhotosDto(updatedPhoto));
    }

    /**
     * Deletes a photo by its unique ID.
     */
    @DeleteMapping("/vehicle-photos/{photoId}")
    public ResponseEntity<Void> deletePhotoById(@PathVariable Long photoId) {
        vehiclePhotosService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    // --- Request DTOs ---

    @Getter @Setter private static class CreatePhotoRequest { private String url; private Integer sortOrder; }
    @Getter @Setter private static class UpdateSortOrderRequest { private Integer newSortOrder; }

}