package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.TeamDetails;
import it.polito.ai.lab2.pojos.TeamProposalDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.services.*;
import it.polito.ai.lab2.utility.ModelHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log(topic = "StudentController")
@RequestMapping("/API/students")
public class StudentController {

  @Autowired
  StudentService studentService;

  @Autowired
  TeamService teamService;

  @Autowired
  CustomUserDetailsService customUserDetailsService;

  @Autowired
  VmService vmService;

  @Autowired
  AssignmentAndUploadService assAndUploadService;

  @GetMapping({"", "/"})
  public List<StudentDTO> all(@RequestParam(required = false, name = "surname_like") String pattern) {
    log.info("all() called");
    if (pattern == null || pattern.isEmpty()) {
      return studentService.getAllStudents().stream()
          .map(ModelHelper::enrich)
          .collect(Collectors.toList());
    } else {
      return studentService.getStudentsLike(pattern).stream()
          .map(ModelHelper::enrich)
          .collect(Collectors.toList());
    }
  }

  @GetMapping("/{id}")
  public StudentDTO getOne(@PathVariable String id) {
    log.info("getOne(" + id + ") called");
    return studentService.getStudent(id)
        .map(ModelHelper::enrich)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student " + id + " does not exist"));
  }

  @GetMapping("/{id}/courses")
  public List<CourseDTO> getCourses(@PathVariable String id) {
    log.info("getCourses(" + id + ") called");
    try {
      return studentService.getStudentCourses(id).stream()
          .map(ModelHelper::enrich)
          .collect(Collectors.toList());
    } catch (StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{id}/teams")
  public List<TeamDTO> getTeams(@PathVariable String id) {
    log.info("getTeams(" + id + ") called");
    try {
      return teamService.getTeamsForStudent(id).stream()
          .map(ModelHelper::enrich)
          .collect(Collectors.toList());
    } catch (StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/teams/{courseId}")
  public TeamDetails getTeamOfStudentOfCourse(@PathVariable String studentId, @PathVariable String courseId) {
    try {
      return teamService.getTeamOfStudentOfCourse(studentId, courseId);
    } catch (StudentNotFoundException | TeamNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/vmsOfCourse/{courseId}")
  public List<VmDTO> getVmsOfStudentOfCourse(@PathVariable String studentId, @PathVariable String courseId) {
    try {
      return vmService.getVmsOfStudentOfCourse(studentId, courseId);
    } catch (CourseNotFoundException | StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (StudentNotInTeamOfCourseException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/ownedVmsOfCourse/{courseId}")
  public List<VmDTO> getOwnedVmsOfStudentOfCourse(@PathVariable String studentId, @PathVariable String courseId) {
    try {
      return vmService.getOwnedVmsOfStudentOfCourse(studentId, courseId);
    } catch (CourseNotFoundException | StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (StudentNotInTeamOfCourseException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/teamProposalsOfCourse/{courseId}")
  public List<TeamProposalDetails> getProposalsForStudentOfCourse(@PathVariable String studentId, @PathVariable String courseId) {
    try {
      return studentService.getProposalsForStudentOfCourse(studentId, courseId);
    } catch (CourseNotFoundException | StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/assignments")
  public List<AssignmentDTO> getAssignments(@PathVariable String studentId) {
    log.info("getAssignments() called");
    try {
      return assAndUploadService.getStudentAssignments(studentId);
    } catch (StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/assignments/{assignmentId}")
  public AssignmentSolutionDTO getAssignmentSolution(@PathVariable String studentId,
                                                     @PathVariable Long assignmentId) {
    log.info("getAssignmentSolution() called");
    try {
      return assAndUploadService.getAssignmentSolutionForAssignmentOfStudent(assignmentId, studentId);
    } catch (StudentNotFoundException | AssignmentSolutionNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
