package com.havelsan.api.repository;

import com.havelsan.api.model.CovidNewsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CovidNewsRepository extends MongoRepository<CovidNewsModel, String> {
    List<CovidNewsModel> findAll();
    List<CovidNewsModel> findByCity(String city);

    List<CovidNewsModel> city(String city);
}
