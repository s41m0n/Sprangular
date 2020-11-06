package it.polito.ai.lab2.services;

import java.util.List;

public interface SecurityService {

  /**
   *
   * @param id The student id
   * @return True if it matches the condition
   */
  boolean isStudentSelf(String id);

  /**
   *
   * @param id The team id
   * @return True if it matches the condition
   */
  boolean isTeamOfStudentCourse(Long id);

  /**
   *
   * @param courseId The course acronym
   * @return True if it matches the condition
   */
  boolean isProfessorCourseOwner(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return True if it matches the condition
   */
  boolean isStudentEnrolled(String courseId);

  /**
   *
   * @param id The team id
   * @return True if it matches the condition
   */
  boolean isTeamOfProfessorCourse(Long id);

  /**
   *
   * @param memberIds The ids of the members in the request
   * @return True if it matches the condition
   */
  boolean isStudentInTeamRequest(List<String> memberIds);

  /**
   *
   * @param vmId The vm id
   * @return True if it matches the condition
   */
  boolean isStudentOwnerOfVm(Long vmId);

  /**
   *
   * @param vmId The vm id
   * @return True if it matches the condition
   */
  boolean isVmOfStudentTeam(Long vmId);

  /**
   *
   * @param teamId The team id
   * @return True if it matches the condition
   */
  boolean isStudentInTeam(Long teamId);

  /**
   *
   * @param assignmentId The assignment id
   * @return True if it matches the condition
   */
  boolean isAssignmentOfProfessorCourse(Long assignmentId);

  /**
   *
   * @param assignmentId The assignment id
   * @return True if it matches the condition
   */
  boolean isAssignmentOfProfessor(Long assignmentId);

  /**
   *
   * @param assignmentId The assignment id
   * @return True if it matches the condition
   */
  boolean hasStudentTheAssignment(Long assignmentId);

  /**
   *
   * @param assignmentSolutionId The assignment solution id
   * @return True if it matches the condition
   */
  boolean isAssignmentSolutionOfProfessorCourse(Long assignmentSolutionId);

  /**
   *
   * @param assignmentSolutionId The assignment solution id
   * @return True if it matches the condition
   */
  boolean isAssignmentSolutionOfStudent(Long assignmentSolutionId);

  /**
   *
   * @param uploadId The upload id
   * @return True if it matches the condition
   */
  boolean isUploadOfProfessorCourse(Long uploadId);

  /**
   *
   * @param uploadId The upload id
   * @return True if it matches the condition
   */
  boolean isUploadOfStudentAssignmentSolution(Long uploadId);
}
