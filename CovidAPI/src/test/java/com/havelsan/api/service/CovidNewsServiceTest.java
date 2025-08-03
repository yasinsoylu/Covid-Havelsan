package com.havelsan.api.service;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.response.ChartResponseDTO;
import com.havelsan.api.model.CovidNewsModel;
import com.havelsan.api.repository.CovidNewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CovidNewsServiceTest {

    @Mock
    private DataProcessingService dataProcessingService;

    @Mock
    private CovidNewsRepository repo;

    @InjectMocks
    private CovidNewsService service;

    private CovidNewsModel sampleNews;
    private String sampleInput;

    @BeforeEach
    void setUp() {
        sampleInput = "20.04.2020 tarihinde Ankara da 15 yeni vaka bulundu. 1 kişi vefat etti. 5 kişi taburcu oldu.";
        sampleNews = new CovidNewsModel(
                LocalDate.of(2020, 4, 20),
                "Ankara", 15, 1, 5, sampleInput
        );
    }

    @Test
    void processAndSaveNews() {
        when(dataProcessingService.parseNewsAndFillModel(sampleInput)).thenReturn(sampleNews);
        when(repo.save(any(CovidNewsModel.class))).thenReturn(sampleNews);

        CovidNewsDTO result = service.processAndSave(sampleInput);

        assertNotNull(result);
        assertEquals(LocalDate.of(2020, 4, 20), result.getDate());
        assertEquals("Ankara", result.getCity());
        assertEquals(15, result.getCaseCount());
        assertEquals(1, result.getDeathCount());
        assertEquals(5, result.getDischargesCount());

        verify(dataProcessingService).parseNewsAndFillModel(sampleInput);
        verify(repo).save(sampleNews);
    }

    @Test
    void getAllNewsFromDatabase() {
        List<CovidNewsModel> newsInDb = Arrays.asList(sampleNews);
        when(repo.findAll()).thenReturn(newsInDb);

        List<CovidNewsDTO> result = service.getAllData();

        assertEquals(1, result.size());
        assertEquals("Ankara", result.get(0).getCity());
        verify(repo).findAll();
    }

    @Test
    void getNewsBySpecificCity() {
        String cityName = "Ankara";
        List<CovidNewsModel> ankaraNews = Arrays.asList(sampleNews);
        when(repo.findByCity(cityName)).thenReturn(ankaraNews);

        List<CovidNewsDTO> result = service.getByCity(cityName);

        assertEquals(1, result.size());
        assertEquals(cityName, result.get(0).getCity());
        verify(repo).findByCity(cityName);
    }

    @Test
    void getDailyChartForAllCities() {
        List<CovidNewsModel> allNews = Arrays.asList(sampleNews);
        when(repo.findAll()).thenReturn(allNews);

        List<ChartResponseDTO> chartData = service.getDailyChartData(
                Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertEquals(1, chartData.size());
        ChartResponseDTO data = chartData.get(0);
        assertEquals(LocalDate.of(2020, 4, 20), data.getDate());
        assertEquals("Turkey Total Count", data.getCity());
        assertEquals(15L, data.getDailyCases());
        assertEquals(1L, data.getDailyDeaths());
        assertEquals(5L, data.getDailyDischarges());
        verify(repo).findAll();
    }

    @Test
    void getDailyChartForSpecificCity() {
        String city = "Ankara";
        List<CovidNewsModel> cityNews = Arrays.asList(sampleNews);
        when(repo.findByCity(city)).thenReturn(cityNews);

        List<ChartResponseDTO> chartData = service.getDailyChartData(
                Optional.of(city), Optional.empty(), Optional.empty()
        );

        assertEquals(1, chartData.size());
        assertEquals(city, chartData.get(0).getCity());
        verify(repo).findByCity(city);
    }

    @Test
    void getCumulativeChartData() {
        CovidNewsModel day1 = new CovidNewsModel(LocalDate.of(2020, 4, 20), "Ankara", 15, 1, 5, "news1");
        CovidNewsModel day2 = new CovidNewsModel(LocalDate.of(2020, 4, 21), "Ankara", 10, 2, 3, "news2");
        List<CovidNewsModel> twoData = Arrays.asList(day1, day2);
        when(repo.findAll()).thenReturn(twoData);

        List<ChartResponseDTO> cumulativeData = service.getCumulativeChartData(
                Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertEquals(2, cumulativeData.size());

        // first day: 15, 1, 5
        ChartResponseDTO firstDay = cumulativeData.get(0);
        assertEquals(15L, firstDay.getTotalCases());
        assertEquals(1L, firstDay.getTotalDeaths());
        assertEquals(5L, firstDay.getTotalDischarges());

        // second day: 10, 2, 3
        // Cumulative: first day + second day => 25, 3, 8
        ChartResponseDTO secondDay = cumulativeData.get(1);
        assertEquals(25L, secondDay.getTotalCases()); // 15 + 10
        assertEquals(3L, secondDay.getTotalDeaths());  // 1 + 2
        assertEquals(8L, secondDay.getTotalDischarges()); // 5 + 3
    }
}