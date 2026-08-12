package com.freshdelivery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.freshdelivery.mapper")
public class FreshDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshDeliveryApplication.class, args);
    }
}