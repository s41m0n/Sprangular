package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.UserDTO;
import it.polito.ai.lab2.pojos.RegistrationDetails;

public interface UserService {
    void registerStudent(RegistrationDetails studentRegistrationDetails);
    void registerProfessor(RegistrationDetails professorRegistrationDetails);
    boolean confirmEmail(String id, String token);
}
