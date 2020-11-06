package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.TeamDTO;

import java.util.List;

public interface NotificationService {

  /**
   * Send a mail
   * @param address The email address
   * @param subject The message subject
   * @param body The message body
   */
  void sendMessage(String address, String subject, String body);

  /**
   * Confirm the team proposal
   * @param token The proposal id
   * @return True if the operation was successful
   */
  boolean confirm(String token);

  /**
   * Reject the team proposal
   * @param token The proposal id
   * @return True if the operation was successful
   */
  boolean reject(String token);

  /**
   * Delete the team proposal from the view
   * @param token The proposal id
   * @return True if the operation was successful
   */
  boolean deleteProposal(String token);

  /**
   * Send a mail with the proposal for the team to all the members but the creator
   * @param dto The team
   * @param memberIds All the id of the members
   * @param courseId The course acronym
   */
  void notifyTeam(TeamDTO dto, List<String> memberIds, String courseId);

}
