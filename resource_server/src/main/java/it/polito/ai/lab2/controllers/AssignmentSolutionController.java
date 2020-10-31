package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.exceptions.AssignmentSolutionNotFoundException;
import it.polito.ai.lab2.services.AssignmentAndUploadService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Log(topic = "AssignmentSolutionController")
@RequestMapping("/API/assignmentSolutions")
public class AssignmentSolutionController {

  @Autowired
  AssignmentAndUploadService assignmentAndUploadService;

  @GetMapping("/{assignmentSolutionId}/uploads")
  public List<UploadDTO> getAssignmentSolutionUploads(@PathVariable Long assignmentSolutionId) {
    log.info("getAssignmentSolutionUploads() called");
    try {
      return assignmentAndUploadService.getStudentUploadsForAssignmentSolution(assignmentSolutionId);
    } catch (AssignmentSolutionNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
