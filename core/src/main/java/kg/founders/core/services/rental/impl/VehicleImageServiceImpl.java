package kg.founders.core.services.rental.impl;

import kg.founders.core.converter.rental.VehicleImageConverter;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.entity.rental.VehicleImage;
import kg.founders.core.model.rental.VehicleImageDto;
import kg.founders.core.repo.VehicleImageRepository;
import kg.founders.core.repo.VehicleRepository;
import kg.founders.core.services.rental.VehicleImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleImageServiceImpl implements VehicleImageService {

    private final VehicleImageRepository vehicleImageRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleImageConverter vehicleImageConverter;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @Transactional(readOnly = true)
    @Override
    public List<VehicleImageDto> getImagesByVehicleId(Long vehicleId) {
        return vehicleImageRepository.findByVehicleIdOrderBySortOrderAsc(vehicleId).stream().map(vehicleImageConverter::convertFromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public VehicleImage getRawById(Long id) {
        return vehicleImageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + id));
    }

    @Transactional
    @Override
    public void delete(Long imageId) {
        try {
            vehicleImageRepository.deleteById(imageId);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException("Vehicle's image not found with id: " + imageId);
        }
    }

    @Override
    public VehicleImageDto upload(Long vehicleId, MultipartFile file, boolean isMain) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("File too large (max 10MB)");

        String mimeType = file.getContentType();
        if (!ALLOWED_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Unsupported type: " + mimeType);
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + vehicleId));

        // Если isMain — сбросить флаг у остальных
        if (isMain) {
            vehicleImageRepository.findByVehicleIdOrderBySortOrderAsc(vehicleId)
                    .forEach(img -> { img.setMain(false); vehicleImageRepository.save(img); });
        }

        int nextOrder = vehicleImageRepository.findByVehicleIdOrderBySortOrderAsc(vehicleId).size();

        VehicleImage image = VehicleImage.builder()
                .vehicle(vehicle)
                .data(file.getBytes())
                .mimeType(mimeType)
                .filename(file.getOriginalFilename())
                .main(isMain)
                .sortOrder(nextOrder)
                .build();

        return vehicleImageConverter.convertFromEntity(vehicleImageRepository.save(image));
    }

    @Override
    public void setMain(Long id) {
        VehicleImage target = getRawById(id);
        vehicleImageRepository.findByVehicleIdOrderBySortOrderAsc(target.getVehicle().getId())
                .forEach(img -> { img.setMain(img.getId().equals(id)); vehicleImageRepository.save(img); });
    }

}