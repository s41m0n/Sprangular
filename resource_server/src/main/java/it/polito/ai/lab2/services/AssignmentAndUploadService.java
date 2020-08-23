package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;

public interface AssignmentAndUploadService {

    boolean createAssignment(AssignmentDTO assignmentDTO, Long courseId); //professor is retrieved from the jwt


}
