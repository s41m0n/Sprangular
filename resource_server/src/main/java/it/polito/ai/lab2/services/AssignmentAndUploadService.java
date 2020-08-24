package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.entities.Assignment;
import it.polito.ai.lab2.entities.AssignmentSolution;
import it.polito.ai.lab2.entities.StudentUpload;
import it.polito.ai.lab2.utility.AssignmentStatus;

import java.util.List;

public interface AssignmentAndUploadService {

    List<Assignment> getAssignmentsForCourse(String courseName);

    List<AssignmentSolution> getAssignmentSolutionsForAssignment(Long assignmentId);

    List<AssignmentSolution> filterAssignmentSolutionsForStatus(Long assignmentId, AssignmentStatus status);

    List<StudentUpload> getStudentsUploadForAssignmentSolution(Long assignmentId, String studentId);

    boolean createAssignment(AssignmentDTO assignmentDTO, String courseName);
    //TODO: il prof lo recuperiamo dall'identità nel jwt?
    //TODO: quando creo un assignment devo anche creare un solution per ogni studente del corso con stato NULL.

    boolean uploadStudentUpload(UploadDTO uploadDTO, Long assignmentSolutionId);

    boolean updateAssignmentSolutionStatus(Long assignmentSolutionId, AssignmentStatus status);

    boolean uploadProfessorUpload(UploadDTO uploadDTO, Long studentUploadId, AssignmentStatus newStatus);

    void automaticDeliveryAfterDueDate();
    //TODO: metodo schedulato che modifica lo stato passata la data di scadenza.

    void assignGrade(Long assignmentSolutionId, String grade);
}
