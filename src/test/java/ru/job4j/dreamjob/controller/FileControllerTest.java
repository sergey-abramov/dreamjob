package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileController fileController;
    private MultipartFile file;
    private FileService fileService;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        file = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    void getById() throws Exception {
        var fileDto = new FileDto(file.getOriginalFilename(), file.getBytes());
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        var actualFile = fileController.getById(1);

        assertThat(actualFile.getBody()).isEqualTo(fileDto.getContent());
    }
}