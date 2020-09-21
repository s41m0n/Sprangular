package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.pojos.SetVmsResourceLimits;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TeamService {

  List<TeamDTO> getTeamsForStudent(String studentId);

  List<StudentDTO> getTeamMembers(Long teamId);

  TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, Timestamp deadline);

  List<TeamDTO> getTeamsForCourse(String courseId);

  List<StudentDTO> getStudentsInTeams(String courseId);

  List<StudentDTO> getAvailableStudents(String courseId);

  void activateTeam(Long id);

  void evictTeam(Long id);

  List<TeamDTO> getTeams();

  Optional<TeamDTO> getTeam(Long id);

  CourseDTO getCourseForTeam(Long id);

  TeamDTO setVmsResourceLimits(Long teamId, SetVmsResourceLimits vmResourceLimits);
}
