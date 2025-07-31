package com.havelsan.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CovidNewsDTO {

    @JsonFormat(pattern = "dd.MM.yyyy",  timezone = "Europe/Istanbul")
    private LocalDate date;

    // It is optional for daily charts.
    private String city;

    private Integer caseCount;
    private Integer deathCount;
    private Integer dischargesCount;
    private String theNews;

    public CovidNewsDTO(LocalDate date, String city, Integer caseCount, Integer deathCount, Integer dischargesCount, String theNews) {
        this.date = date;
        this.city = city;
        this.caseCount = caseCount;
        this.deathCount = deathCount;
        this.dischargesCount = dischargesCount;
        this.theNews = theNews;
    }
}
