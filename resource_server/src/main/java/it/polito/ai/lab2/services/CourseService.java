package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.pojos.CourseWithModelDetails;
import it.polito.ai.lab2.pojos.StudentWithTeamDetails;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

  /**
   *
   * @param course The enriched information of the course
   * @return The created course
   */
  CourseDTO addCourse(CourseWithModelDetails course);

  /**
   *
   * @param courseId The course acronym
   * @return The deleted course
   */
  CourseDTO removeCourse(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @param course The updated course information
   * @return The updated course
   */
  CourseDTO updateCourse(String courseId, CourseWithModelDetails course);

  /**
   *
   * @param courseId The course acronym
   * @return The course if present
   */
  Optional<CourseDTO> getCourse(String courseId);

  /**
   *
   * @return All the courses
   */
  List<CourseDTO> getAllCourses();

  /**
   *
   * @param courseId The course acronym
   * @return The enriched information of the enrolled students
   */
  List<StudentWithTeamDetails> getEnrolledStudents(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return True if the operation was successful
   */
  boolean enableCourse(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return True if the operation was successful
   */
  boolean disableCourse(String courseId);

  /**
   *
   * @param studentId The student id
   * @param courseId The course id
   * @return True if the operation was successful
   */
  boolean addStudentToCourse(String studentId, String courseId);

  /**
   *
   * @param r The stream of the received file
   * @param courseID The course acronym
   * @return True if the operation for the corresponding element was successful
   */
  List<Boolean> enrollAll(Reader r, String courseID);

  /**
   *
   * @param studentId The student id
   * @param courseId The course acronym
   * @return The student removed from the course
   */
  StudentDTO removeStudentFromCourse(String studentId, String courseId);

  /**
   *
   * @param studentIds All the id of the students
   * @param courseId The course acronym
   * @return All the students removed from the course
   */
  List<StudentDTO> removeStudentsFromCourse(List<String> studentIds, String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return All the professor for the course
   */
  List<ProfessorDTO> getCourseProfessors(String courseId);

  /**
   *
   * @param professorId The professor id
   * @param courseId The course acronym
   * @return True if the operation was successful
   */
  boolean addProfessorToCourse(String professorId, String courseId);

  /**
   *
   * @param professorId The professor id
   * @param courseId The course acronym
   * @return The professor removed from the course
   */
  ProfessorDTO removeProfessorFromCourse(String professorId, String courseId);

  /**
   * Select only the students with the surname matching the pattern
   * @param courseId The course acronym
   * @param pattern The substring to match
   * @return The enriched information for the students respecting the pattern
   */
  List<StudentWithTeamDetails> getStudentsOfCourse(String courseId, String pattern);
}
