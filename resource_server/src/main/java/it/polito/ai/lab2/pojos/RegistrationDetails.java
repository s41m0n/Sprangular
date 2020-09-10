package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegistrationDetails {
  String id;
  String email;
  String name;
  String surname;
  String password;
  MultipartFile photo;
}
