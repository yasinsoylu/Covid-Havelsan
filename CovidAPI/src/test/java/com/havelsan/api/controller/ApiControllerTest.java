package com.havelsan.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.request.ParseRequestDTO;
import com.havelsan.api.dto.response.ChartResponseDTO;
import com.havelsan.api.exception.InvalidNewsFormatException;
import com.havelsan.api.service.CovidNewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CovidNewsService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processValidNews() throws Exception {
        ParseRequestDTO request = new ParseRequestDTO();
        request.setInputText("20.04.2020 tarihinde Ankara da 15 yeni vaka bulundu. 1 kişi vefat etti. 5 kişi taburcu oldu.");

        CovidNewsDTO expectedDto = new CovidNewsDTO(
                LocalDate.of(2020, 4, 20),
                "Ankara", 15, 1, 5,
                request.getInputText()
        );

        when(service.processAndSave(request.getInputText())).thenReturn(expectedDto);

        mockMvc.perform(post("/covid/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Ankara"))
                .andExpect(jsonPath("$.caseCount").value(15))
                .andExpect(jsonPath("$.deathCount").value(1))
                .andExpect(jsonPath("$.dischargesCount").value(5));
    }

    @Test
    void rejectInvalidNewsFormat() throws Exception {
        ParseRequestDTO request = new ParseRequestDTO();
        request.setInputText("Test input: Bu geçersiz bir haber formatı");

        when(service.processAndSave(request.getInputText()))
                .thenThrow(new InvalidNewsFormatException("Unable to parse data. Check your format"));

        mockMvc.perform(post("/covid/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid News Format"));
    }

    @Test
    void rejectEmptyNewsText() throws Exception {
        ParseRequestDTO request = new ParseRequestDTO();
        request.setInputText("");

        mockMvc.perform(post("/covid/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void getAllNewsData() throws Exception {
        List<CovidNewsDTO> mockData = Arrays.asList(
                new CovidNewsDTO(LocalDate.of(2020, 4, 20), "Ankara", 15, 1, 5, "news1"),
                new CovidNewsDTO(LocalDate.of(2020, 4, 21), "İstanbul", 30, 3, 7, "news2")
        );

        when(service.getAllData()).thenReturn(mockData);

        mockMvc.perform(get("/covid/api/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].city").value("Ankara"))
                .andExpect(jsonPath("$[1].city").value("İstanbul"));
    }

    @Test
    void getNewsByCity() throws Exception {
        String cityName = "Ankara";
        List<CovidNewsDTO> ankaraData = Arrays.asList(
                new CovidNewsDTO(LocalDate.of(2020, 4, 20), cityName, 15, 1, 5, "news1")
        );

        when(service.getByCity(cityName)).thenReturn(ankaraData);

        mockMvc.perform(get("/covid/api/get/city/{city}", cityName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value(cityName));
    }

    @Test
    void getDailyChartData() throws Exception {
        List<ChartResponseDTO> chartData = Arrays.asList(
                new ChartResponseDTO(LocalDate.of(2020, 4, 20), "Ankara", 15L, 1L, 5L, 15L, 1L, 5L)
        );

        when(service.getDailyChartData(any(Optional.class), any(Optional.class), any(Optional.class)))
                .thenReturn(chartData);

        mockMvc.perform(get("/covid/api/chart/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value("Ankara"))
                .andExpect(jsonPath("$[0].dailyCases").value(15));
    }

    @Test
    void getDailyChartWithFilters() throws Exception {
        List<ChartResponseDTO> chartData = Arrays.asList(
                new ChartResponseDTO(LocalDate.of(2020, 4, 20), "Ankara", 15L, 1L, 5L, 15L, 1L, 5L)
        );

        when(service.getDailyChartData(eq(Optional.of("Ankara")), any(Optional.class), any(Optional.class)))
                .thenReturn(chartData);

        mockMvc.perform(get("/covid/api/chart/daily")
                        .param("city", "Ankara")
                        .param("startDate", "15.04.2020")
                        .param("endDate", "25.04.2020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getCumulativeChartData() throws Exception {
        List<ChartResponseDTO> cumulativeData = Arrays.asList(
                new ChartResponseDTO(LocalDate.of(2020, 4, 20), "Ankara", 15L, 1L, 5L, 15L, 1L, 5L),
                new ChartResponseDTO(LocalDate.of(2020, 4, 21), "Ankara", 25L, 3L, 8L, 10L, 2L, 3L)
        );

        when(service.getCumulativeChartData(any(Optional.class), any(Optional.class), any(Optional.class)))
                .thenReturn(cumulativeData);

        mockMvc.perform(get("/covid/api/chart/cumulative"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].totalCases").value(15))
                .andExpect(jsonPath("$[1].totalCases").value(25));
    }

    @Test
    void rejectInvalidDateRange() throws Exception {
        mockMvc.perform(get("/covid/api/chart/daily")
                        .param("startDate", "25.04.2020")
                        .param("endDate", "15.04.2020"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Argument"));
    }
}