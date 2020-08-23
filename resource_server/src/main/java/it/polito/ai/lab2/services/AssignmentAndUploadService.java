package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.utility.AssignmentStatus;

public interface AssignmentAndUploadService {

    boolean createAssignment(AssignmentDTO assignmentDTO, Long courseId);
    //TODO: il prof lo recuperiamo dall'identità nel jwt?
    //TODO: quando creo un assignment devo anche creare un solution per ogni studente del corso con stato NULL.

    boolean uploadStudentUpload(UploadDTO uploadDTO, Long assignmentSolutionId);

    boolean updateAssignmentSolutionStatus(Long assignmentSolutionId, AssignmentStatus status);

    boolean uploadProfessorUpload(UploadDTO uploadDTO, Long studentUploadId);

    void automaticDeliveryAfterDueDate();
    //TODO: metodo schedulato che modifica lo stato passata la data di scadenza.

    void assignGrade(Long assignmentSolutionId, String grade);
}
