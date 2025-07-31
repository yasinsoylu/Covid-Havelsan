package com.havelsan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CovidApiApplication{

    public static void main(String[] args)
    {
        SpringApplication.run(CovidApiApplication.class, args);
        System.out.println("CovidApiApplication started");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
