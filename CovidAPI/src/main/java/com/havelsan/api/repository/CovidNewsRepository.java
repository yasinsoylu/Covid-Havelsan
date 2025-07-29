package com.havelsan.api.repository;

import com.havelsan.api.model.CovidNews;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CovidNewsRepository extends MongoRepository<CovidNews, String> {
    List<CovidNews> findAll();
    List<CovidNews> findByCity(String city);
}
