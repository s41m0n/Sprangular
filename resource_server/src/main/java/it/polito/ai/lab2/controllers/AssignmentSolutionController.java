package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.exceptions.AssignmentSolutionNotFoundException;
import it.polito.ai.lab2.exceptions.AssignmentSolutionNotReviewedException;
import it.polito.ai.lab2.exceptions.DefinitiveAssignmentSolutionStatusException;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.services.AssignmentAndUploadService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

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

  @PostMapping("/{assignmentSolutionId}/professorUpload")
  public UploadDTO professorUpload(@PathVariable Long assignmentSolutionId,
                                   @ModelAttribute UploadDetails details) {
    log.info("professorUpload() called");
    try {
      return assignmentAndUploadService.uploadProfessorUpload(assignmentSolutionId, details);
    } catch (AssignmentSolutionNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{assignmentSolutionId}/studentUpload")
  public UploadDTO studentUpload(@PathVariable Long assignmentSolutionId,
                                 @ModelAttribute UploadDetails details) {
    log.info("studentUpload() called");
    try {
      return assignmentAndUploadService.uploadStudentUpload(assignmentSolutionId, details);
    } catch (AssignmentSolutionNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{assignmentSolutionId}/grade")
  public AssignmentSolutionDTO evaluateAssignment(@PathVariable Long assignmentSolutionId,
                                                  @RequestBody Map<String, String> grade) {
    log.info("evaluateAssignment() called");
    try {
      return assignmentAndUploadService.assignGrade(assignmentSolutionId, grade.get("grade"));
    } catch (AssignmentSolutionNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (DefinitiveAssignmentSolutionStatusException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    } catch (AssignmentSolutionNotReviewedException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
