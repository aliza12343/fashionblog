package org.example.capstone2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.capstone2", "exception"})
public class Capstone2Application {

    public static void main(String[] args) {
        SpringApplication.run(Capstone2Application.class, args);
    }

}
