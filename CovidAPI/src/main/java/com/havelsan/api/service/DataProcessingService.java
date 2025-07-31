package com.havelsan.api.service;

import com.havelsan.api.model.CovidNewsModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
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


    public static CovidNewsModel parseNewsAndFillModel(String inputText) {

        // Input controlling.
        if (inputText == null || inputText.trim().isEmpty()) {
            return null;
        }

        CovidNewsModel covidNews = new CovidNewsModel();

        // Original News
        covidNews.setTheNews(inputText);

        // Date value detected with string regex operations.
        // It is converted from String to LocalDate and set to the model.
        String dateStr = findDate(inputText);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);
        covidNews.setDate(localDate);

        // City value can be anywhere in text. That's why no need to split as sentences.
        covidNews.setCity(findCity(inputText));

        // We've retrieved the date value. We'll separate the sentences using the [.] regex.
        // However, the [.] in the date value can interfere with sentence separation.
        // Therefore, we're removing it from our input because we already have the date data.
        inputText = inputText.replace(dateStr, "DateValue");

        // Numerical values must be in different sentences, so they will be checked according to the sentences.
        String[] sentences = inputText.split("[.]");
        for (String sentence : sentences) {
            sentence = sentence.trim().toLowerCase();

            if (covidNews.getCaseCount() == null && sentence.contains("vaka")) {
                covidNews.setCaseCount(findNumber(sentence));
            }

            if (covidNews.getDeathCount() == null && sentence.contains("vefat")) {
                covidNews.setDeathCount(findNumber(sentence));
            }

            if (covidNews.getDischargesCount() == null && sentence.contains("taburcu")) {
                covidNews.setDischargesCount(findNumber(sentence));
            }
        }
        System.out.println(covidNews.toString());
        return covidNews;
    }

    private static String findDate(String inputText) {
        // Regex patter for date format: dd.mm.yyyy
        Pattern pattern = Pattern.compile("\\b(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})\\b");
        Matcher matcher = pattern.matcher(inputText);

        if (matcher.find()) {
            return matcher.group();
        } else {return null;}
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
        // Regex patter for number format.
        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
        Matcher matcher = pattern.matcher(inputText);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {return null;}
    }
}
