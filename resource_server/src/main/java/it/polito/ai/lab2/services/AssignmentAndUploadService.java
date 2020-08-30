package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface AssignmentAndUploadService {

    List<AssignmentDTO> getAssignmentsForCourse(String courseName);

    List<AssignmentSolutionDTO> getAssignmentSolutionsForAssignment(Long assignmentId);

    List<AssignmentDTO> getStudentAssignments(String studentId);

    List<AssignmentSolutionDTO> filterAssignmentSolutionsForStatus(Long assignmentId, AssignmentStatus status);

    List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentId, String studentId);

    AssignmentDTO createAssignment(AssignmentDTO assignmentDTO, String courseName, String professorId);

    UploadDTO uploadStudentUpload(UploadDTO uploadDTO, Long assignmentSolutionId);

    Resource getAssignmentForStudent(Long assignmentId, String studentId) throws FileNotFoundException;

    UploadDTO uploadProfessorUpload(UploadDTO uploadDTO, Long studentUploadId, boolean reUploadable);

    void assignGrade(Long assignmentSolutionId, String grade);
}
