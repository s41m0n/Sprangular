package it.polito.ai.lab2;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class SpringExample {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    };

    public static void main(String[] args) {
        SpringApplication.run(SpringExample.class, args);
    }

}
