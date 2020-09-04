package it.polito.ai.lab2.services;

import java.util.List;

public interface SecurityService {

    boolean isStudentSelf(String id);
    boolean isTeamOfStudentCourse(Long id);
    boolean isProfessorCourseOwner(String courseId);
    boolean isStudentEnrolled(String courseId);
    boolean isTeamOfProfessorCourse(Long id);
    boolean isStudentInTeamRequest(List<String> memberIds);
    boolean isStudentOwnerOfVm(Long vmId);
    boolean isVmOfStudentTeam(Long vmId);
    boolean isStudentInTeam(Long teamId);
}
