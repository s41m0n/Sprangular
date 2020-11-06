package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.pojos.SetVmsResourceLimits;
import it.polito.ai.lab2.pojos.TeamDetails;

import java.util.List;
import java.util.Optional;

public interface TeamService {

  /**
   *
   * @param studentId The student id
   * @return The teams of the student
   */
  List<TeamDTO> getTeamsForStudent(String studentId);

  /**
   *
   * @param studentId The student id
   * @param courseId The course acronym
   * @return The enriched information of the team
   */
  TeamDetails getTeamOfStudentOfCourse(String studentId, String courseId);

  /**
   *
   * @param teamId The team id
   * @return All the team members
   */
  List<StudentDTO> getTeamMembers(Long teamId);

  /**
   * Generate the team and, if needed, the corresponding proposals
   * @param courseId The course acronym
   * @param name The team name
   * @param memberIds The members ids
   * @param deadline The proposal deadline
   * @return The created team
   */
  TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, Long deadline);

  /**
   *
   * @param courseId The course acronym
   * @return The teams of the course
   */
  List<TeamDTO> getTeamsForCourse(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return All the students members of any team
   */
  List<StudentDTO> getStudentsInTeams(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return All the students not members of any team
   */
  List<StudentDTO> getAvailableStudents(String courseId);

  /**
   * The student surname must match the pattern
   * @param courseId The course acronym
   * @param pattern The substring to match
   * @return The students matching the rule
   */
  List<StudentDTO> getAvailableStudentsLike(String courseId, String pattern);

  /**
   *
   * @param id The team id
   */
  void activateTeam(Long id);

  /**
   *
   * @param id The team to delete
   */
  void evictTeam(Long id);

  /**
   *
   * @return All the teams
   */
  List<TeamDTO> getTeams();

  /**
   *
   * @param id The team id
   * @return The team if present
   */
  Optional<TeamDTO> getTeam(Long id);

  /**
   *
   * @param id The team id
   * @return The team course
   */
  CourseDTO getCourseForTeam(Long id);

  /**
   * Update the team resources limits
   * @param teamId The team id
   * @param vmResourceLimits The limits to be updated
   * @return The updated team
   */
  TeamDTO setVmsResourceLimits(Long teamId, SetVmsResourceLimits vmResourceLimits);
}
