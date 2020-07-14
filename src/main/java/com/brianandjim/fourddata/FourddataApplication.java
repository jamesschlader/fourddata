package com.brianandjim.fourddata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FourddataApplication {

    public static void main(String[] args) {
        SpringApplication.run(FourddataApplication.class, args);
    }

}
