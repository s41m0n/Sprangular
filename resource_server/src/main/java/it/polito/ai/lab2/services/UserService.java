package it.polito.ai.lab2.services;

import it.polito.ai.lab2.pojos.RegistrationDetails;

public interface UserService {

  /**
   *
   * @param studentRegistrationDetails The information of the registering student
   * @return True if the operation was successful
   */
  boolean registerStudent(RegistrationDetails studentRegistrationDetails);

  /**
   *
   * @param professorRegistrationDetails The information of the registering professor
   * @return True if the operation was successful
   */
  boolean registerProfessor(RegistrationDetails professorRegistrationDetails);

  /**
   *
   * @param id The user id
   * @param token The confirmation token
   * @return True if the operation was successful
   */
  boolean confirmEmail(String id, String token);
}
