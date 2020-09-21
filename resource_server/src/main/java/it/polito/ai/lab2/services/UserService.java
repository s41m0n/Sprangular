package it.polito.ai.lab2.services;

import it.polito.ai.lab2.pojos.RegistrationDetails;

public interface UserService {
  boolean registerStudent(RegistrationDetails studentRegistrationDetails);

  boolean registerProfessor(RegistrationDetails professorRegistrationDetails);

  boolean confirmEmail(String id, String token);
}
