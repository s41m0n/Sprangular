package it.polito.ai.lab2.dtos;

import lombok.Data;

@Data
public class UserDTO {

  String email;

  String password;

  String id;

  String name;

  String surname;

  String photoPath;

  boolean verified;
}
