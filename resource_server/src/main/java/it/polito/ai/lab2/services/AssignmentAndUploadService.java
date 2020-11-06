package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.pojos.AssignmentDetails;
import it.polito.ai.lab2.pojos.AssignmentSolutionDetails;
import it.polito.ai.lab2.pojos.StudentAssignmentDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface AssignmentAndUploadService {

  /**
   *
   * @param courseId The course acronym
   * @return All the assignments of the course
   */
  List<AssignmentDTO> getAssignmentsForCourse(String courseId);

  /**
   *
   * @param courseId The course acronym
   * @return The enriched information of the assignment for the student
   */
  List<StudentAssignmentDetails> getStudentAssignmentsDetails(String courseId);

  /**
   *
   * @param assignmentId The id of the assignment
   * @return The enriched information of the assignment solution for the professor
   */
  List<AssignmentSolutionDetails> getAssignmentSolutionsForAssignment(Long assignmentId);

  /**
   *
   * @param assignmentId The id of the assignment
   * @param studentId The id of the student
   * @return The assignment solution of the student for the assignment
   */
  AssignmentSolutionDTO getAssignmentSolutionForAssignmentOfStudent(Long assignmentId, String studentId);

  /**
   *
   * @param studentId The id of the student
   * @return The assignments for the student
   */
  List<AssignmentDTO> getStudentAssignments(String studentId);

  /**
   *
   * @param assignmentSolutionId The id of the assignment solution
   * @return The uploads of the assignment solution
   */
  List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentSolutionId);

  /**
   *
   * @param details The information to create the assignment
   * @param courseId The course acronym
   * @param professorId The id of the professor
   * @return The created assignment
   */
  AssignmentDTO createAssignment(AssignmentDetails details, String courseId, String professorId);

  /**
   *
   * @param assignmentSolutionId The id of the assignment solution
   * @param uploadDetails The information of the student upload
   * @return The created upload
   */
  UploadDTO uploadStudentUpload(Long assignmentSolutionId, UploadDetails uploadDetails);

  /**
   *
   * @param assignmentSolutionId The id of the assignment solution
   * @param uploadDetails The information of the professor upload
   * @return The created Upload
   */
  UploadDTO uploadProfessorUpload(Long assignmentSolutionId, UploadDetails uploadDetails);

  /**
   *
   * @param assignmentId The id of the assignment
   * @return The document of the assignment for the student
   * @throws FileNotFoundException If the file does not exist
   */
  Resource getAssignmentForStudent(Long assignmentId) throws FileNotFoundException;

  /**
   *
   * @param assignmentId The id of the assignment
   * @return The document of the assignment for the professor
   * @throws FileNotFoundException If the file does not exist
   */
  Resource getAssignmentDocument(Long assignmentId) throws FileNotFoundException;

  /**
   *
   * @param assignmentSolutionId The id of the assignment solution
   * @param grade The grade assigned to the solution
   * @return The definitive version of the assignment solution
   */
  AssignmentSolutionDTO assignGrade(Long assignmentSolutionId, String grade);

  /**
   *
   * @param uploadId The id of the upload
   * @return The document of the upload
   * @throws FileNotFoundException If the file does not exist
   */
  Resource getUploadDocument(Long uploadId) throws FileNotFoundException;
}
