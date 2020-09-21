package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.exceptions.StudentUploadNotFoundException;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.services.AssignmentAndUploadService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/API/uploads")
@Log(topic = "AssignmentUploadController")
public class AssignmentUploadController {

  @Autowired
  AssignmentAndUploadService assAndUploadService;

  @PostMapping("/{uploadId}/review")
  public UploadDTO professorUpload(@PathVariable Long uploadId,
                                   @ModelAttribute UploadDetails details) {
    log.info("professorUpload() called");
    try {
      return assAndUploadService.uploadProfessorUpload(details, uploadId);
    } catch (StudentUploadNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
