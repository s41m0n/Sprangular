package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.exceptions.AssignmentNotFoundException;
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
@Log(topic = "AssignmentController")
@RequestMapping("/API/assignments")
public class AssignmentController {

  @Autowired
  AssignmentAndUploadService assAndUploadService;

  @GetMapping("/{assignmentId}/solutions")
  public List<AssignmentSolutionDTO> solutionsForAssignment(@PathVariable Long assignmentId) {
    log.info("solutionsForAssignment() called");
    try {
      return assAndUploadService.getAssignmentSolutionsForAssignment(assignmentId);
    } catch (AssignmentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}