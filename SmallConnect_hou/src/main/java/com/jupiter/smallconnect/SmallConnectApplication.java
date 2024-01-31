package com.jupiter.smallconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling  //开启定时注解
@SpringBootApplication
public class SmallConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmallConnectApplication.class, args);
    }

}
