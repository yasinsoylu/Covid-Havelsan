package com.havelsan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CovidApiApplication{

    public static void main(String[] args)
    {
        SpringApplication.run(CovidApiApplication.class, args);
        System.out.println("CovidApiApplication started");
    }

}
