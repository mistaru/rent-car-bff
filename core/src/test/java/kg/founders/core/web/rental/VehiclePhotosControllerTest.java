package kg.founders.core.web.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.rental.VehiclePhotos;
import kg.founders.core.model.rental.VehiclePhotosDto;
import kg.founders.core.services.rental.VehiclePhotosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiclePhotosController.class)
class VehiclePhotosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehiclePhotosService vehiclePhotosService;

    @MockBean
    private RentalMapper rentalMapper;

    @Test
    void getPhotosForVehicle_ShouldReturnList() throws Exception {
        Long vehicleId = 1L;
        VehiclePhotos photo = VehiclePhotos.builder().id(10L).url("url").sortOrder(1).build();
        VehiclePhotosDto dto = VehiclePhotosDto.builder().id(10L).url("url").sortOrder(1).build();

        when(vehiclePhotosService.getPhotosByVehicleId(vehicleId)).thenReturn(List.of(photo));
        when(rentalMapper.toVehiclePhotosDto(photo)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/vehicles/{vehicleId}/photos", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].url").value("url"));
    }

    @Test
    void addPhotoToVehicle_ShouldReturnCreated() throws Exception {
        Long vehicleId = 1L;
        String url = "http://test.com/img.jpg";
        Integer sortOrder = 1;
        
        // JSON body matching CreatePhotoRequest
        String requestBody = String.format("{\"url\":\"%s\", \"sortOrder\":%d}", url, sortOrder);

        VehiclePhotos photo = VehiclePhotos.builder().id(100L).url(url).sortOrder(sortOrder).build();
        VehiclePhotosDto dto = VehiclePhotosDto.builder().id(100L).url(url).sortOrder(sortOrder).build();

        when(vehiclePhotosService.addPhoto(eq(vehicleId), eq(url), eq(sortOrder))).thenReturn(photo);
        when(rentalMapper.toVehiclePhotosDto(photo)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/photos", vehicleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    void getPhotoById_ShouldReturnPhoto() throws Exception {
        Long photoId = 10L;
        VehiclePhotos photo = VehiclePhotos.builder().id(photoId).url("url").build();
        VehiclePhotosDto dto = VehiclePhotosDto.builder().id(photoId).url("url").build();

        when(vehiclePhotosService.getPhoto(photoId)).thenReturn(photo);
        when(rentalMapper.toVehiclePhotosDto(photo)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/vehicle-photos/{photoId}", photoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(photoId));
    }

    @Test
    void updatePhotoSortOrder_ShouldReturnUpdatedPhoto() throws Exception {
        Long photoId = 10L;
        Integer newSortOrder = 99;
        
        // JSON body matching UpdateSortOrderRequest
        String requestBody = String.format("{\"newSortOrder\":%d}", newSortOrder);

        VehiclePhotos photo = VehiclePhotos.builder().id(photoId).sortOrder(newSortOrder).build();
        VehiclePhotosDto dto = VehiclePhotosDto.builder().id(photoId).sortOrder(newSortOrder).build();

        when(vehiclePhotosService.updatePhotoSortOrder(eq(photoId), eq(newSortOrder))).thenReturn(photo);
        when(rentalMapper.toVehiclePhotosDto(photo)).thenReturn(dto);

        mockMvc.perform(put("/api/v1/vehicle-photos/{photoId}/sort-order", photoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortOrder").value(newSortOrder));
    }

    @Test
    void deletePhotoById_ShouldReturnNoContent() throws Exception {
        Long photoId = 10L;

        doNothing().when(vehiclePhotosService).deletePhoto(photoId);

        mockMvc.perform(delete("/api/v1/vehicle-photos/{photoId}", photoId))
                .andExpect(status().isNoContent());
    }
}