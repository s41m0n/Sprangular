package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.exceptions.UploadNotFoundException;
import it.polito.ai.lab2.services.AssignmentAndUploadService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;

@RestController
@Log(topic = "UploadController")
@RequestMapping("/API/uploads")
public class UploadController {

  @Autowired
  AssignmentAndUploadService assignmentAndUploadService;

  @GetMapping("/{uploadId}/document")
  public Resource getUploadDocument(@PathVariable Long uploadId) {
    log.info("getUploadDocument() called");
    try {
      return assignmentAndUploadService.getUploadDocument(uploadId);
    } catch (UploadNotFoundException | FileNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
