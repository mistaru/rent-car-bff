package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.entity.rental.VehiclePhotos;
import kg.founders.core.repo.VehiclePhotosRepository;
import kg.founders.core.repo.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiclePhotosServiceTest {

    @Mock
    private VehiclePhotosRepository vehiclePhotosRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehiclePhotosService vehiclePhotosService;

    @Test
    void getPhotosByVehicleId_ShouldReturnList() {
        Long vehicleId = 1L;
        VehiclePhotos photo1 = VehiclePhotos.builder().id(1L).sortOrder(1).build();
        VehiclePhotos photo2 = VehiclePhotos.builder().id(2L).sortOrder(2).build();
        
        when(vehiclePhotosRepository.findAllByVehicleIdOrderBySortOrderAsc(vehicleId))
                .thenReturn(Arrays.asList(photo1, photo2));

        List<VehiclePhotos> result = vehiclePhotosService.getPhotosByVehicleId(vehicleId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(vehiclePhotosRepository).findAllByVehicleIdOrderBySortOrderAsc(vehicleId);
    }

    @Test
    void addPhoto_ShouldSaveAndReturnPhoto() {
        Long vehicleId = 1L;
        String url = "http://example.com/image.jpg";
        Integer sortOrder = 5;
        Vehicle vehicleProxy = new Vehicle();
        vehicleProxy.setId(vehicleId);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleProxy));
        when(vehiclePhotosRepository.save(any(VehiclePhotos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehiclePhotos result = vehiclePhotosService.addPhoto(vehicleId, url, sortOrder);

        assertNotNull(result);
        assertEquals(url, result.getUrl());
        assertEquals(sortOrder, result.getSortOrder());
        assertEquals(vehicleId, result.getVehicle().getId());
        verify(vehicleRepository).findById(vehicleId);
        verify(vehiclePhotosRepository).save(any(VehiclePhotos.class));
    }

    @Test
    void updatePhotoSortOrder_ShouldUpdateAndSave() {
        Long photoId = 10L;
        Integer newSortOrder = 2;
        VehiclePhotos existingPhoto = VehiclePhotos.builder().id(photoId).sortOrder(1).build();

        when(vehiclePhotosRepository.findById(photoId)).thenReturn(Optional.of(existingPhoto));
        when(vehiclePhotosRepository.save(any(VehiclePhotos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehiclePhotos result = vehiclePhotosService.updatePhotoSortOrder(photoId, newSortOrder);

        assertEquals(newSortOrder, result.getSortOrder());
        verify(vehiclePhotosRepository).save(existingPhoto);
    }

    @Test
    void updatePhotoSortOrder_WhenNotFound_ShouldThrowException() {
        Long photoId = 99L;
        when(vehiclePhotosRepository.findById(photoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            vehiclePhotosService.updatePhotoSortOrder(photoId, 5)
        );
    }

    @Test
    void deletePhoto_ShouldDelete_WhenExists() {
        Long photoId = 10L;

        vehiclePhotosService.deletePhoto(photoId);

        verify(vehiclePhotosRepository).deleteById(photoId);
    }
}