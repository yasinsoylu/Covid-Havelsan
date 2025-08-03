package com.havelsan.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

// BASE MODEL
@Data
@Document(collection = "news_infos")
public class CovidNewsModel {

    @Id
    private String id;

    @Indexed
    private String city;

    @Indexed
    @JsonFormat(pattern = "dd.MM.yyyy",  timezone = "Europe/Istanbul")
    @Field("date")
    private LocalDate date;

    private Integer caseCount;
    private Integer deathCount;
    private Integer dischargesCount;
    private String theNews;

    public CovidNewsModel() {
        // Necessary for MongoDB
    }

    public CovidNewsModel(LocalDate date, String city, Integer caseCount, Integer deathCount, Integer dischargesCount, String theNews) {
        this.date = date;
        this.city = city;
        this.caseCount = caseCount;
        this.deathCount = deathCount;
        this.dischargesCount = dischargesCount;
        this.theNews = theNews;
    }

    @Override
    public String toString() {
        return "CovidNewsModel{" +
                "id='" + id + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", caseCount=" + caseCount +
                ", deathCount=" + deathCount +
                ", dischargesCount=" + dischargesCount +
                ", theNews='" + theNews + '\'' +
                '}';
    }

    public boolean isValid() {
        if (this.date != null && this.city != null && this.caseCount != null && this.deathCount != null
                && this.dischargesCount != null && this.theNews != null) {
            return true;
        }
        return false;
    }
}
