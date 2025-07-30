package com.havelsan.api.service;

import com.havelsan.api.dto.CovidNewsDTO;
import com.havelsan.api.model.CovidNewsModel;
import com.havelsan.api.repository.CovidNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
