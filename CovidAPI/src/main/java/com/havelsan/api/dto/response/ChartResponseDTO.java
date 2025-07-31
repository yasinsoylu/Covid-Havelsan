package com.havelsan.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponseDTO {
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;

    private String city;

    private Long totalCases;
    private Long totalDeaths;
    private Long totalDischarges;
    private Long dailyCases;
    private Long dailyDeaths;
    private Long dailyDischarges;
}
