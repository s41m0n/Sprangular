package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {

    List<ProfessorDTO> getProfessors();

    boolean addProfessor(ProfessorDTO professorDTO);

    Optional<ProfessorDTO> getProfessor(String id);

    List<CourseDTO> getProfessorCourses(String id);
}
