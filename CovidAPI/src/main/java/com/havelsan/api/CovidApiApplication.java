package com.havelsan.api;

import com.havelsan.api.model.CovidNews;
import com.havelsan.api.repository.CovidNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.havelsan.api.service.DataProcessingService.fillInfoModel;

@SpringBootApplication
public class CovidApiApplication implements CommandLineRunner{

    @Autowired
    private CovidNewsRepository covidNewsRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(CovidApiApplication.class, args);
        System.out.println("CovidApiApplication started");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== COVID NEWS UYGULAMASI BAŞLATILDI ===");
        System.out.println("MongoDB Bağlantısı: " + (covidNewsRepository != null ? "BAŞARILI" : "BAŞARISIZ"));

        String newsText = "19.04.2020 tarihinde izmir için korona virüs ile ilgili yeni bir açıklama yapıldı. Korona virüs salgınında yapılan testlerde 20 yeni vaka tespit edildi. taburcu sayısı ise 7 oldu.  3 kişin de vefat ettiği öğrenildi.";
        CovidNews infos = fillInfoModel(newsText);
        System.out.println("Date: " + infos.getDate());
        System.out.println("City: " + infos.getCity());

        // Save to the repository
        covidNewsRepository.save(infos);

//        List<CovidNews> allNews = covidNewsRepository.findByCity("İstanbul");
//        for (CovidNews covidNews : allNews) {
//            System.out.println(covidNews);
//        }
    }
}
