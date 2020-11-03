package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.AssignmentDetails;
import it.polito.ai.lab2.pojos.AssignmentSolutionDetails;
import it.polito.ai.lab2.pojos.StudentAssignmentDetails;
import it.polito.ai.lab2.pojos.UploadDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.AssignmentStatus;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Date;
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
  UploadRepository uploadRepository;

  @Autowired
  TaskScheduler scheduler;

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId) " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<AssignmentDTO> getAssignmentsForCourse(String courseId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    return course.getAssignments().stream()
        .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<StudentAssignmentDetails> getStudentAssignmentsDetails(String courseId) {
    if (!courseRepository.existsById(courseId))
        throw  new CourseNotFoundException("Course " + courseId + " does not exist");
    List<AssignmentSolution> ass = assignmentSolutionRepository.findByStudentId(
        SecurityContextHolder.getContext().getAuthentication().getName()).stream()
        .filter(as -> as.getAssignment().getCourse().getAcronym().equals(courseId))
        .collect(Collectors.toList());
    return ass.stream()
        .map(as -> {
          StudentAssignmentDetails sad = new StudentAssignmentDetails();
          sad.setAssignmentId(as.getAssignment().getId());
          sad.setName(as.getAssignment().getName());
          sad.setReleaseDate(as.getAssignment().getReleaseDate());
          sad.setDueDate(as.getAssignment().getDueDate());
          sad.setAssignmentSolutionId(as.getId());
          sad.setStatus(as.getStatus());
          sad.setGrade(as.getGrade());
          sad.setStatusTs(as.getStatusTs());
          return sad;
        })
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentOfProfessor(#assignmentId) and @securityServiceImpl.isAssignmentOfProfessorCourse(#assignmentId)")
  public List<AssignmentSolutionDetails> getAssignmentSolutionsForAssignment(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist"));
    return assignment.getSolutions().stream()
        .map(solution -> {
          AssignmentSolutionDetails asd = new AssignmentSolutionDetails();
          asd.setId(solution.getId());
          asd.setStudentName(solution.getStudent().getName());
          asd.setStudentSurname(solution.getStudent().getSurname());
          asd.setStudentId(solution.getStudent().getId());
          asd.setStatus(solution.getStatus());
          asd.setStatusTs(solution.getStatusTs());
          asd.setGrade(solution.getGrade());
          return asd;
        })
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentOfProfessor(#assignmentId) " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId) and @securityServiceImpl.hasStudentTheAssignment(#assignmentId)")
  public AssignmentSolutionDTO getAssignmentSolutionForAssignmentOfStudent(Long assignmentId, String studentId) {
    if (!studentRepository.existsById(studentId))
      throw new StudentNotFoundException("Student " + studentId + " not found");
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(
        assignmentId, studentId).orElseThrow(() -> new AssignmentSolutionNotFoundException(
        "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    return modelMapper.map(assignmentSolution, AssignmentSolutionDTO.class);
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
  public List<AssignmentDTO> getStudentAssignments(String studentId) {
    if (!studentRepository.existsById(studentId))
      throw new StudentNotFoundException("Student " + studentId + " does not exist");
    return assignmentSolutionRepository.findByStudentId(studentId).stream()
        .map(solution -> modelMapper.map(solution.getAssignment(), AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentSolutionOfProfessorCourse(#assignmentSolutionId)" +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isAssignmentSolutionOfStudent(#assignmentSolutionId)")
  public List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentSolutionId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId).orElseThrow(
        () -> new AssignmentSolutionNotFoundException(
        "Assignment solution " + assignmentSolutionId + " does not exist"));
    return assignmentSolution.getUploads().stream()
        .map(upload -> modelMapper.map(upload, UploadDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public AssignmentDTO createAssignment(AssignmentDetails details, String courseId, String professorId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    Professor professor = professorRepository.findById(professorId)
        .orElseThrow(() -> new ProfessorNotFoundException("Professor " + professorId + " does not exist"));
    Assignment assignment = new Assignment();
    assignment.setName(details.getName());
    Timestamp currentTs = new Timestamp(System.currentTimeMillis());
    assignment.setReleaseDate(currentTs);
    assignment.setDueDate(new Timestamp(details.getDueDate()));
    assignment.setCourse(course);
    assignment.setProfessor(professor);
    course.getStudents().forEach(student -> {
      AssignmentSolution assignmentSolution = new AssignmentSolution();
      assignmentSolution.setAssignment(assignment);
      assignment.getSolutions().add(assignmentSolution);
      assignmentSolution.setStudent(student);
      assignmentSolution.setStatus(AssignmentStatus.NULL);
      assignmentSolution.setStatusTs(currentTs);
      assignmentSolutionRepository.save(assignmentSolution);
      Upload upload = new Upload();
      upload.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
      upload.setTimestamp(currentTs);
      upload.setStatus(AssignmentStatus.NULL);
      upload.setComment("Assignment published");
      upload.setAssignmentSolution(assignmentSolution);
      uploadRepository.save(upload);
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
        assignmentSolution -> {
          Timestamp innerCurrentTs = new Timestamp(System.currentTimeMillis());
          assignmentSolution.setStatus(AssignmentStatus.DELIVERED);
          assignmentSolution.setStatusTs(innerCurrentTs);
          assignmentSolutionRepository.save(assignmentSolution);
          Upload upload = new Upload();
          upload.setAuthor(assignmentSolution.getStudent().getId());
          upload.setTimestamp(innerCurrentTs);
          upload.setStatus(AssignmentStatus.DELIVERED);
          upload.setComment("Assignment automatically delivered");
          upload.setAssignmentSolution(assignmentSolution);
          uploadRepository.save(upload);
        }
    );
    scheduler.schedule(automaticDelivery, new Date(assignment.getDueDate().getTime()));
    return modelMapper.map(savedAssignment, AssignmentDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_STUDENT')and @securityServiceImpl.isAssignmentSolutionOfStudent(#assignmentSolutionId)")
  public UploadDTO uploadStudentUpload(Long assignmentSolutionId, UploadDetails uploadDetails) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution " + assignmentSolutionId + " does not exist"));
    if (!isUploadable(assignmentSolution.getStatus()))
      throw new UploadNotAllowedException("Cannot upload for assignment solution " + assignmentSolutionId
          + ": solution status is " + assignmentSolution.getStatus().toString());

    Timestamp currentTs = new Timestamp(System.currentTimeMillis());
    Upload upload = new Upload();
    upload.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
    upload.setTimestamp(currentTs);
    upload.setComment(uploadDetails.getComment());
    upload.setAssignmentSolution(assignmentSolution);
    upload.setStatus(AssignmentStatus.DELIVERED);
    assignmentSolution.getUploads().add(upload);
    assignmentSolution.setStatus(AssignmentStatus.DELIVERED);
    assignmentSolution.setStatusTs(currentTs);

    Upload savedUpload = uploadRepository.save(upload);
    Path uploadPath = Utility.UPLOADS_DIR.resolve(savedUpload.getId().toString());
    savedUpload.setImagePath(uploadPath.toString());
    try {
      Files.copy(uploadDetails.getDocument().getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    return modelMapper.map(savedUpload, UploadDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.hasStudentTheAssignment(#assignmentId)")
  public Resource getAssignmentForStudent(Long assignmentId) throws FileNotFoundException {
    String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignmentIdAndStudentId(
        assignmentId, studentId).orElseThrow(
            () -> new AssignmentSolutionNotFoundException(
        "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    Resource file = null;
    try {
      file = new UrlResource(Paths.get(assignmentSolution.getAssignment().getImagePath()).toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (file == null)
      throw new FileNotFoundException("Assignment" + assignmentId + " image not found");
    if (assignmentSolution.getStatus().equals(AssignmentStatus.NULL)) {
      Timestamp currentTs = new Timestamp(System.currentTimeMillis());
      assignmentSolution.setStatus(AssignmentStatus.READ);
      assignmentSolution.setStatusTs(currentTs);
      Upload upload = new Upload();
      upload.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
      upload.setTimestamp(currentTs);
      upload.setStatus(AssignmentStatus.READ);
      upload.setComment("Assignment read");
      upload.setAssignmentSolution(assignmentSolution);
      uploadRepository.save(upload);
    }
    return file;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentOfProfessorCourse(#assignmentId)")
  public Resource getAssignmentDocument(Long assignmentId) throws FileNotFoundException {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist"));
    Resource file = null;
    try {
      file = new UrlResource(Paths.get(assignment.getImagePath()).toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (file == null)
      throw new FileNotFoundException("Assignment" + assignmentId + " image not found");
    return file;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentSolutionOfProfessorCourse(#assignmentSolutionId)")
  public UploadDTO uploadProfessorUpload(Long assignmentSolutionId, UploadDetails details) {
    AssignmentSolution assSolution = assignmentSolutionRepository.findById(assignmentSolutionId).orElseThrow(
        () -> new AssignmentSolutionNotFoundException("Assignment solution " + assignmentSolutionId + "does not exist"));
    if (!assSolution.getStatus().equals(AssignmentStatus.DELIVERED))
      throw new UploadNotAllowedException("Student assignment status is not DELIVERED");
    Timestamp currentTs = new Timestamp(System.currentTimeMillis());
    Upload professorUpload = new Upload();
    professorUpload.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
    professorUpload.setTimestamp(currentTs);
    professorUpload.setComment(details.getComment());
    professorUpload.setAssignmentSolution(assSolution);
    if (details.isReUploadable()) {
      professorUpload.setStatus(AssignmentStatus.REVIEWED_UPLOADABLE);
      assSolution.setStatus(AssignmentStatus.REVIEWED_UPLOADABLE);
    } else {
      professorUpload.setStatus(AssignmentStatus.REVIEWED);
      assSolution.setStatus(AssignmentStatus.REVIEWED);
    }
    assSolution.setStatusTs(currentTs);

    Upload savedUpload = uploadRepository.save(professorUpload);
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
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isAssignmentSolutionOfProfessorCourse(#assignmentSolutionId)")
  public AssignmentSolutionDTO assignGrade(Long assignmentSolutionId, String grade) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution " + assignmentSolutionId + " does not exist"));
    if (assignmentSolution.getStatus().equals(AssignmentStatus.DEFINITIVE))
      throw new DefinitiveAssignmentSolutionStatusException(
          "Assignment solution " + assignmentSolutionId + " status already definitive");
    if (!assignmentSolution.getStatus().equals(AssignmentStatus.REVIEWED)
        && !assignmentSolution.getStatus().equals(AssignmentStatus.REVIEWED_UPLOADABLE))
      throw new AssignmentSolutionNotReviewedException(
          "Assignment solution " + assignmentSolutionId + " not reviewed yet");
    assignmentSolution.setGrade(grade);
    assignmentSolution.setStatus(AssignmentStatus.DEFINITIVE);
    Timestamp currentTs = new Timestamp(System.currentTimeMillis());
    assignmentSolution.setStatusTs(currentTs);
    Upload upload = new Upload();
    upload.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
    upload.setTimestamp(currentTs);
    upload.setStatus(AssignmentStatus.DEFINITIVE);
    upload.setComment("Grade registered: " + grade);
    upload.setAssignmentSolution(assignmentSolution);
    uploadRepository.save(upload);
    return modelMapper.map(assignmentSolution, AssignmentSolutionDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') " +
      "or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isUploadOfProfessorCourse(#uploadId)" +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isUploadOfStudentAssignmentSolution(#uploadId)")
  public Resource getUploadDocument(Long uploadId) throws FileNotFoundException {
    Upload upload = uploadRepository.findById(uploadId)
        .orElseThrow(() -> new UploadNotFoundException("Upload " + uploadId + " does not exist"));
    Resource file = null;
    try {
      file = new UrlResource(Paths.get(upload.getImagePath()).toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (file == null)
      throw new FileNotFoundException("Upload " + uploadId + " image not found");
    return file;
  }

  private boolean isUploadable(AssignmentStatus assignmentStatus) {
    return assignmentStatus.equals(AssignmentStatus.READ)
        || assignmentStatus.equals(AssignmentStatus.REVIEWED_UPLOADABLE);
  }
}
