package com.havelsan.api.service;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.response.ChartResponseDTO;
import com.havelsan.api.model.CovidNewsModel;
import com.havelsan.api.repository.CovidNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CovidNewsService {
    @Autowired
    private DataProcessingService dataProcessingService;
    @Autowired
    private CovidNewsRepository repo;

    public CovidNewsDTO processAndSave(String inputText){
        System.out.println("[INFO] Input text: " + inputText);
        CovidNewsModel covidNewsModel = dataProcessingService.parseNewsAndFillModel(inputText);
        repo.save(covidNewsModel);
        return convertToDTO(covidNewsModel);
    }

    public List<CovidNewsDTO> getAllData(){
        return repo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<CovidNewsDTO> getByCity(String city){
        return repo.findByCity(city).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // startDate <= date <= endDate
    public List<CovidNewsDTO> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return repo.findByDateBetween(startDate.minusDays(1), endDate.plusDays(1)).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // City value is optional, startDate <= date <= endDate
    public List<ChartResponseDTO> getDailyChartData(Optional<String> city, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        List<CovidNewsModel> covidNewsModel;

        if (city.isPresent()) {
            if (startDate.isPresent() && endDate.isPresent()) {
                covidNewsModel = repo.findByCityAndDateBetween(city.get(), startDate.get().minusDays(1), endDate.get().plusDays(1));
            } else {
                covidNewsModel = repo.findByCity(city.get());
            }
        } else {
            if (startDate.isPresent() && endDate.isPresent()) {
                covidNewsModel = repo.findByDateBetween(startDate.get().minusDays(1), endDate.get().plusDays(1));
            } else {
                covidNewsModel = repo.findAll();
            }
        }

        return covidNewsModel.stream()
                .collect(Collectors.groupingBy(info -> info.getDate())).entrySet().stream().map(entry -> {
                    LocalDate date = entry.getKey();
                    List<CovidNewsModel> dailyInfo = entry.getValue();

                    long dailyCases = dailyInfo.stream().mapToLong(n -> n.getCaseCount() != null ? n.getCaseCount() : 0).sum();
                    long dailyDeaths = dailyInfo.stream().mapToLong(n -> n.getDeathCount() != null ? n.getDeathCount() : 0).sum();
                    long dailyDischarges = dailyInfo.stream().mapToLong(n -> n.getDischargesCount() != null ? n.getDischargesCount() : 0).sum();

                    return new ChartResponseDTO(date, city.orElse("Turkey Total Count"),
                            dailyCases, dailyDeaths, dailyDischarges,
                            dailyCases, dailyDeaths, dailyDischarges);
                }).sorted((a, b) -> a.getDate().compareTo(b.getDate())).collect(Collectors.toList());
    }

    // City value is optional => Cumulative = Sum of the All Daily Records
    public List<ChartResponseDTO> getCumulativeChartData(Optional<String> city, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        List<ChartResponseDTO> dailyData = getDailyChartData(city, startDate, endDate);

        long cumulativeCases = 0;
        long cumulativeDeaths = 0;
        long cumulativeDischarges = 0;

        for (ChartResponseDTO data : dailyData) {
            cumulativeCases += data.getDailyCases();
            cumulativeDeaths += data.getDailyDeaths();
            cumulativeDischarges += data.getDailyDischarges();

            data.setTotalCases(cumulativeCases);
            data.setTotalDeaths(cumulativeDeaths);
            data.setTotalDischarges(cumulativeDischarges);
        }

        return dailyData;
    }

    private CovidNewsDTO convertToDTO(CovidNewsModel covidNewsModel) {
        return new CovidNewsDTO(
                covidNewsModel.getDate(),
                covidNewsModel.getCity(),
                covidNewsModel.getCaseCount(),
                covidNewsModel.getDeathCount(),
                covidNewsModel.getDischargesCount(),
                covidNewsModel.getTheNews()
        );
    }
}
