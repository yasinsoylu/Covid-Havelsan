package com.havelsan.api.controller;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.dto.request.ParseRequestDTO;
import com.havelsan.api.repository.CovidNewsRepository;
import com.havelsan.api.service.CovidNewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                System.out.println("[INFO] DTO: " + dto.toString());
                return ResponseEntity.ok(dto);
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


}
