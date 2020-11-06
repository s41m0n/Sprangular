package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {

  /**
   *
   * @return
   */
  List<ProfessorDTO> getProfessors();

  List<ProfessorDTO> getProfessorsLike(String pattern);

  Optional<ProfessorDTO> getProfessor(String professorId);

  List<CourseDTO> getProfessorCourses(String professorId);
}
