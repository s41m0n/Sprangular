package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.pojos.AssignmentDetails;
import it.polito.ai.lab2.pojos.AssignmentSolutionDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface AssignmentAndUploadService {

  List<AssignmentDTO> getAssignmentsForCourse(String courseId);

  List<AssignmentSolutionDetails> getAssignmentSolutionsForAssignment(Long assignmentId);

  AssignmentSolutionDTO getAssignmentSolutionForAssignmentOfStudent(Long assignmentId, String studentId);

  List<AssignmentDTO> getStudentAssignments(String studentId);

  // TODO: do we really need this?
  List<AssignmentSolutionDTO> filterAssignmentSolutionsForStatus(Long assignmentId, AssignmentStatus status);

  List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentId, String studentId);

  AssignmentDTO createAssignment(AssignmentDetails details, String courseId, String professorId);

  UploadDTO uploadStudentUpload(UploadDetails uploadDetails, String studentId, Long assignmentId);

  Resource getAssignmentForStudent(Long assignmentId, String studentId) throws FileNotFoundException;

  Resource getAssignmentDocument(Long assignmentId) throws FileNotFoundException;

  UploadDTO uploadProfessorUpload(UploadDetails uploadDetails, Long studentUploadId);

  AssignmentSolutionDTO assignGrade(String studentId, Long assignmentId, String grade);
}
