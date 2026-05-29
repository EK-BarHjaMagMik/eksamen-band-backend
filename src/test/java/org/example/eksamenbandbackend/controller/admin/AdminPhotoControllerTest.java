package org.example.eksamenbandbackend.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminPhotoControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PhotoService photoService;

        @BeforeEach
        void setUp() throws Exception {
                LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
                validator.afterPropertiesSet();

                mockMvc = MockMvcBuilders.standaloneSetup(new AdminPhotoController(photoService))
                                .setValidator(validator)
                                .build();
        }

        @Test
        void uploadPhotos_returnsUploadedListOnSuccess() throws Exception {
                UploadPhotosResponse.UploadedPhoto up = new UploadPhotosResponse.UploadedPhoto(1L,
                                "/uploads/photo.jpg");
                UploadPhotosResponse resp = new UploadPhotosResponse(List.of(up), List.of());

                when(photoService.uploadPhotos(anyList(), anyString(), anyString(), any(), isNull())).thenReturn(resp);

                MockMultipartFile file = new MockMultipartFile("files", "photo.jpg", "image/jpeg",
                                "imgdata".getBytes());

                mockMvc.perform(multipart("/api/admin/photos")
                                .file(file)
                                .param("caption", "cap")
                                .param("photographer", "ph")
                                .param("dateTaken", LocalDate.of(2026, 5, 1).toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uploaded[0].id").value(1))
                                .andExpect(jsonPath("$.uploaded[0].url").value("/uploads/photo.jpg"))
                                .andExpect(jsonPath("$.errors").isEmpty());
        }

        @Test
        void uploadPhotos_returnsErrorsWhenServiceReports() throws Exception {
                UploadPhotosResponse.UploadError err = new UploadPhotosResponse.UploadError("bad.jpg",
                                "Unsupported file type");
                UploadPhotosResponse resp = new UploadPhotosResponse(List.of(), List.of(err));

                when(photoService.uploadPhotos(anyList(), isNull(), isNull(), isNull(), isNull())).thenReturn(resp);

                MockMultipartFile file = new MockMultipartFile("files", "bad.jpg", "application/pdf",
                                "pdfdata".getBytes());

                mockMvc.perform(multipart("/api/admin/photos").file(file))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uploaded").isEmpty())
                                .andExpect(jsonPath("$.errors[0].filename").value("bad.jpg"))
                                .andExpect(jsonPath("$.errors[0].reason").value("Unsupported file type"));
        }

        @Test
        void updatePhoto_returnsUpdatedPhotoOnSuccess() throws Exception {
                PhotoResponse response = new PhotoResponse(
                                7L,
                                "/uploads/updated.jpg",
                                null,
                                null,
                                "Updated caption",
                                LocalDate.of(2026, 5, 2),
                                "Updated photographer",
                                null);

                when(photoService.updatePhoto(eq(7L), any())).thenReturn(response);

                mockMvc.perform(put("/api/admin/photos/7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "caption": "Updated caption",
                                                    "dateTaken": "2026-05-02",
                                                    "photographer": "Updated photographer",
                                                    "showId": 3
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(7))
                                .andExpect(jsonPath("$.url").value("/uploads/updated.jpg"))
                                .andExpect(jsonPath("$.caption").value("Updated caption"))
                                .andExpect(jsonPath("$.dateTaken").value("2026-05-02"))
                                .andExpect(jsonPath("$.photographer").value("Updated photographer"));
        }

        @Test
        void updatePhoto_returnsBadRequestWhenRequestBodyIsInvalid() throws Exception {
                mockMvc.perform(put("/api/admin/photos/7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updatePhoto_returnsNotFoundWhenPhotoDoesNotExist() throws Exception {
                when(photoService.updatePhoto(eq(99L), any()))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Photo not found with id: 99"));

                mockMvc.perform(put("/api/admin/photos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "caption": "Updated caption",
                                                    "dateTaken": "2026-05-02",
                                                    "photographer": "Updated photographer",
                                                    "showId": 3
                                                }
                                                """))
                                .andExpect(status().isNotFound());
        }

        @Test
        void updatePhoto_returnsBadRequestWhenShowDoesNotExist() throws Exception {
                when(photoService.updatePhoto(eq(7L), any()))
                                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Show with ID 3 not found"));

                mockMvc.perform(put("/api/admin/photos/7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "caption": "Updated caption",
                                                    "dateTaken": "2026-05-02",
                                                    "photographer": "Updated photographer",
                                                    "showId": 3
                                                }
                                                """))
                                .andExpect(status().isBadRequest());
        }
}
