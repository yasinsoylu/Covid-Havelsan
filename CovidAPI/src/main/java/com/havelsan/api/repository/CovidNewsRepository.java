package com.havelsan.api.repository;

import com.havelsan.api.model.CovidNewsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CovidNewsRepository extends MongoRepository<CovidNewsModel, String> {

    List<CovidNewsModel> findAll();
    List<CovidNewsModel> findByCity(String city);

    // startDate <= date <= endDate
    List<CovidNewsModel> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // City is optional && startDate <= date <= endDate
    List<CovidNewsModel> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);
}
