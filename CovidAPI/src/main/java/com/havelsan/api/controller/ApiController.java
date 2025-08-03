package com.havelsan.api.controller;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.request.ParseRequestDTO;
import com.havelsan.api.dto.response.ChartResponseDTO;
import com.havelsan.api.exception.InvalidNewsFormatException;
import com.havelsan.api.repository.CovidNewsRepository;
import com.havelsan.api.service.CovidNewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// CONTROLLER CLASS
@RestController
@RequestMapping("covid/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 7200)
@Validated
@Slf4j
public class ApiController {

    private final CovidNewsService service;

    @PostMapping("/process")
    public ResponseEntity<CovidNewsDTO> initParsing(@Valid @RequestBody ParseRequestDTO parseRequestDTO) {
        log.info("[INFO] Processing started..");

        try {
            CovidNewsDTO dto = service.processAndSave(parseRequestDTO.getInputText());
            if (dto != null) {
                log.info("[INFO] Successfully processed news data: {}" , dto);
                return ResponseEntity.ok(dto);
            } else  {
                throw new InvalidNewsFormatException("Unable to parse data. Check your format");
            }
        } catch (Exception err) {
            log.error("Error processing news data: {}", err.getMessage(), err);
            throw err;
        }
    }

    // Getting all the data from db.
    @GetMapping("/get/all")
    public ResponseEntity<List<CovidNewsDTO>> getAll() {
        List<CovidNewsDTO> dto = service.getAllData();
        log.info("Retrieved {} records", dto.size());
        return ResponseEntity.ok(dto);
    }

    // Getting all the data for spesific city.
    @GetMapping("/get/city/{city}")
    public ResponseEntity<List<CovidNewsDTO>> getByCity(@PathVariable String city) {
        List<CovidNewsDTO> dto = service.getByCity(city);
        log.info("Retrieved {} records for city: {}", dto.size(), city);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/chart/daily")
    public ResponseEntity<List<ChartResponseDTO>> getDailyChartData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<ChartResponseDTO> chartData = service.getDailyChartData(
                Optional.ofNullable(city),
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate)
        );
        log.info("Retrieved {} daily chart records", chartData.size());
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/chart/cumulative")
    public ResponseEntity<List<ChartResponseDTO>> getCumulativeChartData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        List<ChartResponseDTO> chartData = service.getCumulativeChartData(
                Optional.ofNullable(city),
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate)
        );
        log.info("Retrieved {} cumulative chart records", chartData.size());
        return ResponseEntity.ok(chartData);
    }

}
