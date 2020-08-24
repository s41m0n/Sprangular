package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.utility.TeamProposalDetails;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<StudentDTO> getTeamMembers(Long teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, int hoursTimeout);
    //TODO: dobbiamo implementare la cosa del timeout variabile, una volta il token aveva durata fissa.

    List<TeamDTO> getTeamsForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    void activeTeam(Long id);

    void evictTeam(Long id);

    List<TeamDTO> getTeams();

    Optional<TeamDTO> getTeam(Long id);

    CourseDTO getCourseForTeam(Long id);

    boolean updateVmResourceLimits(TeamDTO teamDTO);
    //TODO: meglio mettere tutti i campi singoli o il DTO che ha 2 campi in più?

    TeamProposalDetails getProposalsForStudentOfCourse(String studentId, String courseName);
    //TODO: classe di appoggio con tutti i dettagli da mandare che sono un casino

}
