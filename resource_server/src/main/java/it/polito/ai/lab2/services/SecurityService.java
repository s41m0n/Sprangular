package it.polito.ai.lab2.services;

import java.util.List;

public interface SecurityService {

    boolean isStudentSelf(String id);
    boolean isTeamOfStudentCourse(Long id);
    boolean isProfessorCourseOwner(String courseName);
    boolean isStudentEnrolled(String courseName);
    boolean isTeamOfProfessorCourse(Long id);
    boolean isStudentInTeamRequest(List<String> memberIds);
}
