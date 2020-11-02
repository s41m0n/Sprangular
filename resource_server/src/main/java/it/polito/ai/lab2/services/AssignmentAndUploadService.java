package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.pojos.AssignmentDetails;
import it.polito.ai.lab2.pojos.AssignmentSolutionDetails;
import it.polito.ai.lab2.pojos.StudentAssignmentDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface AssignmentAndUploadService {

  List<AssignmentDTO> getAssignmentsForCourse(String courseId);

  List<StudentAssignmentDetails> getStudentAssignmentsDetails(String courseId);

  List<AssignmentSolutionDetails> getAssignmentSolutionsForAssignment(Long assignmentId);

  AssignmentSolutionDTO getAssignmentSolutionForAssignmentOfStudent(Long assignmentId, String studentId);

  List<AssignmentDTO> getStudentAssignments(String studentId);

  List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentSolutionId);

  AssignmentDTO createAssignment(AssignmentDetails details, String courseId, String professorId);

  UploadDTO uploadStudentUpload(Long assignmentSolutionId, UploadDetails uploadDetails);

  UploadDTO uploadProfessorUpload(Long assignmentSolutionId, UploadDetails uploadDetails);

  Resource getAssignmentForStudent(Long assignmentId) throws FileNotFoundException;

  Resource getAssignmentDocument(Long assignmentId) throws FileNotFoundException;

  AssignmentSolutionDTO assignGrade(Long assignmentSolutionId, String grade);
}
