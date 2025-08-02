package com.havelsan.api.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ParseRequestDTO {

    @NotNull(message = "Input text cannot be null")
    @NotBlank(message = "Input text cannot be empty or blank")
    private String inputText;
}
