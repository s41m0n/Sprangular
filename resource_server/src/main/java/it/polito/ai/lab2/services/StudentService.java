package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.pojos.TeamProposalDetails;

import java.util.List;
import java.util.Optional;

public interface StudentService {

  /**
   *
   * @param studentId The student id
   * @return The student if present
   */
  Optional<StudentDTO> getStudent(String studentId);

  /**
   *
   * @return All the students
   */
  List<StudentDTO> getAllStudents();

  /**
   *
   * @param studentId The student id
   * @return All the courses of the student
   */
  List<CourseDTO> getStudentCourses(String studentId);

  /**
   *
   * @param studentId The student id
   * @param courseId The course acronym
   * @return The enriched information of the proposals for the student in the course
   */
  List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseId);

  /**
   * The surname of the students must match the pattern
   * @param pattern The substring to match
   * @return The students matching the rule
   */
  List<StudentDTO> getStudentsLike(String pattern);
}
