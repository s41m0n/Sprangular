package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.utility.TeamProposalDetails;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<StudentDTO> getTeamMembers(Long teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, int hoursTimeout);
    //TODO: dobbiamo implementare la cosa del timeout variabile, una volta il token aveva durata fissa.

    List<TeamDTO> getTeamsForCourse(String courseId);

    List<StudentDTO> getStudentsInTeams(String courseId);

    List<StudentDTO> getAvailableStudents(String courseId);

    void activeTeam(Long id);

    void evictTeam(Long id);

    List<TeamDTO> getTeams();

    Optional<TeamDTO> getTeam(Long id);

    CourseDTO getCourseForTeam(Long id);

    List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseId);
    //TeamProposalDetails contains all the details needed by the frontend

    boolean setVmsResourceLimits(Long teamId, int vCpu, int diskStorage, int ram, int maxActiveInstances, int maxTotalInstances);
}
