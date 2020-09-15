package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.AssignmentDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.AssignmentStatus;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentAndUploadServiceImpl implements AssignmentAndUploadService {

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  AssignmentRepository assignmentRepository;

  @Autowired
  AssignmentSolutionRepository assignmentSolutionRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  StudentUploadRepository studentUploadRepository;

  @Autowired
  ProfessorUploadRepository professorUploadRepository;

  @Autowired
  TaskScheduler scheduler;

  //TODO: add more integrity checks

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId) or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<AssignmentDTO> getAssignmentsForCourse(String courseId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    return course.getAssignments().stream()
        .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentOfProfessor(#assignmentId) and @securityServiceImpl.isAssignmentOfProfessorCourse(#assignmentId)")
  public List<AssignmentSolutionDTO> getAssignmentSolutionsForAssignment(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist"));
    return assignment.getSolutions().stream()
        .map(solution -> modelMapper.map(solution, AssignmentSolutionDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
  public List<AssignmentDTO> getStudentAssignments(String studentId) {
    if (!studentRepository.existsById(studentId))
      throw new StudentNotFoundException("Student " + studentId + " does not exist");
    return assignmentSolutionRepository.findByStudentId(studentId).stream()
        .map(solution -> modelMapper.map(solution.getAssignment(), AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentOfProfessor(#assignmentId) and @securityServiceImpl.isAssignmentOfProfessorCourse(#assignmentId)")
  public List<AssignmentSolutionDTO> filterAssignmentSolutionsForStatus(Long assignmentId, AssignmentStatus status) {
    if (!assignmentRepository.existsById(assignmentId))
      throw new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist");
    return assignmentSolutionRepository.findByAssignmentIdAndStatus(assignmentId, status).stream()
        .map(solution -> modelMapper.map(solution, AssignmentSolutionDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentId, String studentId) {
    if (!studentRepository.existsById(studentId))
      throw new StudentNotFoundException("Student " + studentId + " not found");
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(
        assignmentId, studentId).orElseThrow(() -> new AssignmentSolutionNotFoundException(
        "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    List<UploadDTO> toReturn = new ArrayList<>();
    assignmentSolution.getStudentUploads().forEach(
        studentUpload -> {
          toReturn.add(modelMapper.map(studentUpload, UploadDTO.class));
          ProfessorUpload professorUpload = studentUpload.getTeacherRevision();
          if (professorUpload != null)
            toReturn.add(modelMapper.map(professorUpload, UploadDTO.class));
        }
    );
    return toReturn;
  }

  @Override
  public AssignmentDTO createAssignment(AssignmentDetails details, String courseId, String professorId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    Professor professor = professorRepository.findById(professorId)
        .orElseThrow(() -> new ProfessorNotFoundException("Professor " + professorId + " does not exist"));
    Assignment assignment = new Assignment();
    assignment.setName(details.getName());
    assignment.setReleaseDate(details.getReleaseDate());
    assignment.setDueDate(details.getDueDate());
    assignment.setCourse(course);
    assignment.setProfessor(professor);
    course.getStudents().forEach(student -> {
      AssignmentSolution assignmentSolution = new AssignmentSolution();
      assignmentSolution.setAssignment(assignment);
      assignment.getSolutions().add(assignmentSolution);
      assignmentSolution.setStudent(student);
      assignmentSolution.setStatus(AssignmentStatus.NULL);
      assignmentSolutionRepository.save(assignmentSolution);
    });
    Assignment savedAssignment = assignmentRepository.save(assignment);
    Path assignmentPath = Utility.ASSIGNMENTS_DIR.resolve(savedAssignment.getId().toString());
    savedAssignment.setImagePath(assignmentPath.toString());
    try {
      Files.copy(details.getDocument().getInputStream(), assignmentPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    Runnable automaticDelivery = () -> assignment.getSolutions().forEach(
        assignmentSolution -> assignmentSolution.setStatus(AssignmentStatus.DELIVERED)
    );
    scheduler.schedule(automaticDelivery, new CronTrigger(Utility.timestampToCronTrigger(assignment.getDueDate())));
    return modelMapper.map(savedAssignment, AssignmentDTO.class);
  }

  @Override
  public UploadDTO uploadStudentUpload(UploadDetails details, String studentId, Long assignmentId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    StudentUpload studentUpload = new StudentUpload();
    studentUpload.setTimestamp(new Timestamp(System.currentTimeMillis()));
    studentUpload.setComment(details.getComment());
    studentUpload.setAssignmentSolution(assignmentSolution);
    assignmentSolution.getStudentUploads().add(studentUpload);
    assignmentSolution.setStatus(AssignmentStatus.DELIVERED);

    StudentUpload savedUpload = studentUploadRepository.save(studentUpload);
    Path uploadPath = Utility.UPLOADS_DIR.resolve(savedUpload.getId().toString());
    savedUpload.setImagePath(uploadPath.toString());
    try {
      Files.copy(details.getDocument().getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    return modelMapper.map(savedUpload, UploadDTO.class);
  }

  @Override
  public Resource getAssignmentForStudent(Long assignmentId, String studentId) throws FileNotFoundException {
    if (!studentRepository.existsById(studentId))
      throw new StudentNotFoundException("Student " + studentId + " does not exist");
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(
        assignmentId, studentId).orElseThrow(() -> new AssignmentSolutionNotFoundException(
        "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    Resource file = null;
    try {
      file = new UrlResource(assignmentSolution.getAssignment().getImagePath());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (file == null)
      throw new FileNotFoundException("Assignment" + assignmentId + " image not found");
    assignmentSolution.setStatus(AssignmentStatus.READ);
    return file;
  }

  @Override
  public UploadDTO uploadProfessorUpload(UploadDetails details, Long studentUploadId) {
    StudentUpload studentUpload = studentUploadRepository.findById(studentUploadId)
        .orElseThrow(() -> new StudentUploadNotFoundException("Student upload " + studentUploadId + " does not exist"));
    ProfessorUpload professorUpload = new ProfessorUpload();
    professorUpload.setTimestamp(new Timestamp(System.currentTimeMillis()));
    professorUpload.setComment(details.getComment());
    studentUpload.setTeacherRevision(professorUpload);
    professorUpload.setRevisedSolution(studentUpload);
    if (details.isReUploadable())
      studentUpload.getAssignmentSolution().setStatus(AssignmentStatus.REVIEWED_UPLOADABLE);
    else
      studentUpload.getAssignmentSolution().setStatus(AssignmentStatus.REVIEWED);

    ProfessorUpload savedUpload = professorUploadRepository.save(professorUpload);
    Path uploadPath = Utility.UPLOADS_DIR.resolve(savedUpload.getId().toString());
    savedUpload.setImagePath(uploadPath.toString());
    try {
      Files.copy(details.getDocument().getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    return modelMapper.map(savedUpload, UploadDTO.class);
  }

  @Override
  public AssignmentSolutionDTO assignGrade(String studentId, Long assignmentId, String grade) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    if (assignmentSolution.getStatus().equals(AssignmentStatus.DEFINITIVE))
      throw new DefinitiveAssignmentSolutionStatusException(
          "Assignment solution for assignment " + assignmentId + " and student " + studentId + " status already definitive");
    if (!assignmentSolution.getStatus().equals(AssignmentStatus.REVIEWED))
      throw new AssignmentSolutionNotReviewedException(
          "Assignment solution for assignment " + assignmentId + " and student " + studentId + " not reviewed yet");
    assignmentSolution.setGrade(grade);
    assignmentSolution.setStatus(AssignmentStatus.DEFINITIVE);
    return modelMapper.map(assignmentSolution, AssignmentSolutionDTO.class);
  }
}
