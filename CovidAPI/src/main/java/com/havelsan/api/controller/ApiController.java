package com.havelsan.api.controller;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.request.ParseRequestDTO;
import com.havelsan.api.dto.response.ChartResponseDTO;
import com.havelsan.api.repository.CovidNewsRepository;
import com.havelsan.api.service.CovidNewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("covid/api")
@CrossOrigin(origins = "*", maxAge = 7200)
public class ApiController {

    @Autowired
    private CovidNewsService service;
    @Autowired
    private CovidNewsService covidNewsService;

    @PostMapping("/process")
    public ResponseEntity<?> initParsing(@RequestBody ParseRequestDTO parseRequestDTO) {

        try {
            System.out.println("[INFO] === COVID NEWS UYGULAMASI BAŞLATILDI ===");

            // Input Text should not be empty.
            if (parseRequestDTO.getInputText().trim().isEmpty() ||  parseRequestDTO.getInputText() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The news can not be empty");
            } else {
                CovidNewsDTO dto = service.processAndSave(parseRequestDTO.getInputText());
                if (dto != null) {
                    System.out.println("[INFO] DTO: " + dto.toString());
                    return ResponseEntity.ok(dto);
                } else  {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The news can not be empty");
                }

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Getting all the data from db.
    @GetMapping("/get/all")
    public ResponseEntity<List<CovidNewsDTO>> getAll() {
        List<CovidNewsDTO> dto = covidNewsService.getAllData();
        return ResponseEntity.ok(dto);
    }

    // Getting all the data for spesific city.
    @GetMapping("/get/city/{city}")
    public ResponseEntity<List<CovidNewsDTO>> getByCity(@PathVariable String city) {
        List<CovidNewsDTO> dto = covidNewsService.getByCity(city);
        return ResponseEntity.ok(dto);
    }

    // startDate < date < endDate
    @GetMapping("/get/date-range")
    public ResponseEntity<List<CovidNewsDTO>> getNewsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {
        try {
            List<CovidNewsDTO> news = covidNewsService.getByDateRange(startDate, endDate);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/chart/daily")
    public ResponseEntity<List<ChartResponseDTO>> getDailyChartData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        try {
            List<ChartResponseDTO> chartData = covidNewsService.getDailyChartData(
                    Optional.ofNullable(city),
                    Optional.ofNullable(startDate),
                    Optional.ofNullable(endDate)
            );
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/chart/cumulative")
    public ResponseEntity<List<ChartResponseDTO>> getCumulativeChartData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        try {
            List<ChartResponseDTO> chartData = covidNewsService.getCumulativeChartData(
                    Optional.ofNullable(city),
                    Optional.ofNullable(startDate),
                    Optional.ofNullable(endDate)
            );
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
