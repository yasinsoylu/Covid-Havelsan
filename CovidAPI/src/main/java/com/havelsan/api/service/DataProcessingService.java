package com.havelsan.api.service;

import com.havelsan.api.exception.DataProcessingException;
import com.havelsan.api.exception.InvalidNewsFormatException;
import com.havelsan.api.model.CovidNewsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DataProcessingService {
    private static final List<String> cities = Arrays.asList("Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray",
            "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin", "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun",
            "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul", "İzmir", "Kahramanmaraş", "Karabük",
            "Karaman", "Kars", "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir", "Kilis", "Kocaeli",
            "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu",
            "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Şanlıurfa", "Şırnak", "Tekirdağ",
            "Tokat", "Trabzon", "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})\\b");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b(\\d+)\\b");

    public CovidNewsModel parseNewsAndFillModel(String inputText) {

        // Input controlling.
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new InvalidNewsFormatException("Input text can not be null or empty");
        }

        try {
            CovidNewsModel covidNews = new CovidNewsModel();
            covidNews.setTheNews(inputText);

            String dateStr = findDate(inputText);
            log.info("DateStr value is: {} ",dateStr);
            if (dateStr == null) {
                throw new InvalidNewsFormatException("Date not found in the news text. Expected format: dd.MM.yyyy");
            }

            // Parse and set: Date
            LocalDate localDate = parseDate(dateStr);
            covidNews.setDate(localDate);

            // Parse and set: City
            String city = findCity(inputText);
            if (city == null) {
                throw new InvalidNewsFormatException("City not found in the news text.");
            }
            covidNews.setCity(city);

            // Remove date from text to avoid interference with sentence splitting.
            String processedText = inputText.replace(dateStr, "DateValue");

            // Case, Death and Discharge count parsing.
            parseCovidValues(processedText, covidNews);

            // Validate Required Fields:
            validateCovidNewsModel(covidNews);

            log.info("Successfully parsed COVID data: {}", covidNews);
            return covidNews;

        } catch (InvalidNewsFormatException e) {
            log.error("Invalid news format: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during news parsing: {}", e.getMessage(), e);
            throw new DataProcessingException("Failed to process news data", e);
        }
    }

    private void parseCovidValues(String inputText, CovidNewsModel covidNews) {
        String[] sentences = inputText.split("[.]");

        for (String sentence : sentences) {
            String normalizedSentence = sentence.trim().toLowerCase(new Locale("tr", "TR"));

            if (covidNews.getCaseCount() == null && normalizedSentence.contains("vaka")) {
                Integer caseCount = findNumber(normalizedSentence);
                if (caseCount != null && caseCount >= 0) {
                    covidNews.setCaseCount(caseCount);
                    log.debug("Found case count: {}", caseCount);
                }
            }

            if (covidNews.getDeathCount() == null && normalizedSentence.contains("vefat")) {
                Integer deathCount = findNumber(normalizedSentence);
                if (deathCount != null && deathCount >= 0) {
                    covidNews.setDeathCount(deathCount);
                    log.debug("Found death count: {}", deathCount);
                }
            }

            if (covidNews.getDischargesCount() == null && normalizedSentence.contains("taburcu")) {
                Integer dischargeCount = findNumber(normalizedSentence);
                if (dischargeCount != null && dischargeCount >= 0) {
                    covidNews.setDischargesCount(dischargeCount);
                    log.debug("Found discharge count: {}", dischargeCount);
                }
            }
        }
    }

    private void validateCovidNewsModel(CovidNewsModel covidNews) {
        if (covidNews.getCaseCount() == null) {
            throw new InvalidNewsFormatException("Case count (vaka) not found in the news text");
        }
        if (covidNews.getDeathCount() == null) {
            throw new InvalidNewsFormatException("Death count (vefat) not found in the news text");
        }
        if (covidNews.getDischargesCount() == null) {
            throw new InvalidNewsFormatException("Discharge count (taburcu) not found in the news text");
        }
    }

    private static String findDate(String inputText) {
        Matcher matcher = DATE_PATTERN.matcher(inputText);
        return matcher.find() ? matcher.group() : null;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidNewsFormatException("Invalid date format: " + dateStr + ". Expected format: dd.MM.yyyy");
        }
    }

    private static String findCity(String inputText) {
        // Normalize input for turkish characters.
        String normalizedText = inputText.toLowerCase(new Locale("tr", "TR")).replaceAll("[\\p{Punct}]", " ");;

        // Split to words.
        String[] words = normalizedText.split("\\s+");

        for (String city : cities) {
            // Normalize city values for turkish characters.
            String normalizedCity = city.trim().toLowerCase(new Locale("tr", "TR"));
            for (String word : words) {
                if (word.equals(normalizedCity)) {
                    return city;
                }
            }
        }
        return null;
    }

    private static Integer findNumber(String inputText) {
        Matcher matcher = NUMBER_PATTERN.matcher(inputText);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
