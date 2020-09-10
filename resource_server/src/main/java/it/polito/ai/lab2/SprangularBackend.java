package it.polito.ai.lab2;

import it.polito.ai.lab2.utility.Utility;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log
@SpringBootApplication
public class SprangularBackend {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    };

    public static void main(String[] args) {
        SpringApplication.run(SprangularBackend.class, args);
    }

    @Bean
    CommandLineRunner getCommandLineRunner() {
        return args -> {
            try {
                Files.createDirectory(Paths.get(Utility.imagesRootDir));
                Files.createDirectory(Utility.photosDir);
                Files.createDirectory(Utility.assignmentsDir);
                Files.createDirectory(Utility.uploadsDir);
                Files.createDirectory(Utility.vmModelsDir);
                Files.createDirectory(Utility.vmsDir);
                log.info("Uploads directory created");
            } catch (FileAlreadyExistsException e) {
                log.info("Uploads directory already exists");
            }
        };
    }
}
