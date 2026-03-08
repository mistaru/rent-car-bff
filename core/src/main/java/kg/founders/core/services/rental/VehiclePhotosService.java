package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.entity.rental.VehiclePhotos;
import kg.founders.core.repo.VehiclePhotosRepository;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiclePhotosService {

    private final VehiclePhotosRepository vehiclePhotosRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<VehiclePhotos> getPhotosByVehicleId(Long vehicleId) {
        return vehiclePhotosRepository.findAllByVehicleIdOrderBySortOrderAsc(vehicleId);
    }

    @Transactional
    public VehiclePhotos addPhoto(Long vehicleId, String url, Integer sortOrder) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + vehicleId));

        VehiclePhotos photo = VehiclePhotos.builder()
                .vehicle(vehicle)
                .url(url)
                .sortOrder(sortOrder)
                .build();

        return vehiclePhotosRepository.save(photo);
    }

    @Transactional
    public VehiclePhotos updatePhotoSortOrder(Long photoId, Integer newSortOrder) {
        VehiclePhotos photo = vehiclePhotosRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle's photo not found with id: " + photoId));
        
        photo.setSortOrder(newSortOrder);
        return vehiclePhotosRepository.save(photo);
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        try {
            vehiclePhotosRepository.deleteById(photoId);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException("Vehicle's photo not found with id: " + photoId);
        }
    }
    
    public VehiclePhotos getPhoto(Long photoId) {
        return vehiclePhotosRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle's photo not found with id: " + photoId));
    }

}