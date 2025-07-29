package com.havelsan.api.service;

import com.havelsan.api.model.CovidNews;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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


    public static CovidNews fillInfoModel(String inputText) {

        // Input controlling.
        if (inputText == null || inputText.trim().isEmpty()) {
            return null;
        }

        CovidNews covidNews = new CovidNews();

        // City and date values can be anywhere in text. That's why no need to split as sentences.
        covidNews.setDate(findDate(inputText));
        covidNews.setCity(findCity(inputText));

        // We've retrieved the date value. We'll separate the sentences using the [.] regex.
        // However, the [.] in the date value can interfere with sentence separation.
        // Therefore, we're removing it from our input because we already have the date data.
        inputText = inputText.replace(covidNews.getDate(), "DateValue");

        // Numerical values must be in different sentences, so they will be checked according to the sentences.
        String[] sentences = inputText.split("[.]");
        for (String sentence : sentences) {
            sentence = sentence.trim().toLowerCase();
            if (sentence.contains("vaka")) {
                System.out.println("Vaka: " + sentence);
            } else if (sentence.contains("vefat")) {
                System.out.println("Vefat: " + sentence);
            } else if (sentence.contains("taburcu")) {
                System.out.println("Taburcu: " + sentence);
            }
        }

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
        for (String city : cities) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(city) + "\\b", Pattern.CASE_INSENSITIVE  | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(inputText);
            if (matcher.find()) {
                return city;
            }
        }
        return null;
    }
}
