package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<StudentDTO> getTeamMembers(Long teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);

    List<TeamDTO> getTeamForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    void activeTeam(Long id);

    void evictTeam(Long id);

    List<TeamDTO> getTeams();

    Optional<TeamDTO> getTeam(Long id);

    CourseDTO getCourseForTeam(Long id);

}
