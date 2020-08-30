package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.AssignmentSolutionDTO;
import it.polito.ai.lab2.dtos.UploadDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
  public List<AssignmentDTO> getAssignmentsForCourse(String courseName) {
    Course course = courseRepository.findByName(courseName)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    return course.getAssignments().stream()
        .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<AssignmentSolutionDTO> getAssignmentSolutionsForAssignment(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist"));
    return assignment.getSolutions().stream()
        .map(solution -> modelMapper.map(solution, AssignmentSolutionDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<AssignmentDTO> getStudentAssignments(String studentId) {
    if (!studentRepository.existsById(studentId))
        throw new StudentNotFoundException("Student " + studentId + " does not exist");
    return assignmentSolutionRepository.findByStudent_Id(studentId).stream()
        .map(solution -> modelMapper.map(solution.getAssignment(), AssignmentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<AssignmentSolutionDTO> filterAssignmentSolutionsForStatus(Long assignmentId, AssignmentStatus status) {
    if (!assignmentRepository.existsById(assignmentId))
      throw new AssignmentNotFoundException("Assignment " + assignmentId + " does not exist");
    return assignmentSolutionRepository.findByAssignment_IdAndStatus(assignmentId, status).stream()
        .map(solution -> modelMapper.map(solution, AssignmentSolutionDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<UploadDTO> getStudentUploadsForAssignmentSolution(Long assignmentId, String studentId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignment_IdAndStudent_Id(
        assignmentId, studentId).orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution for assignment " + assignmentId + " and student " + studentId + " does not exist"));
    return assignmentSolution.getStudentUploads().stream()
        .map(upload -> modelMapper.map(upload, UploadDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO, String courseName, String professorId) {
    Course course = courseRepository.findByName(courseName)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    Professor professor = professorRepository.findById(professorId)
        .orElseThrow(() -> new ProfessorNotFoundException("Professor " + professorId + " does not exist"));
    Assignment assignment = modelMapper.map(assignmentDTO, Assignment.class);
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
    Runnable automaticDelivery = () -> {
      assignment.getSolutions().forEach(
          assignmentSolution -> assignmentSolution.setStatus(AssignmentStatus.DELIVERED)
      );
    };
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(assignment.getDueDate().getTime());
    String formattedDate = calendar.get(Calendar.MINUTE)
        + " " + calendar.get(Calendar.HOUR)
        + " " + calendar.get(Calendar.DAY_OF_MONTH)
        + " " + (calendar.get(Calendar.MONTH) + 1)
        + " *"
        + " " + calendar.get(Calendar.YEAR);
    scheduler.schedule(automaticDelivery, new CronTrigger(formattedDate));
    return modelMapper.map(assignmentRepository.save(assignment), AssignmentDTO.class);
  }

  @Override
  public UploadDTO uploadStudentUpload(UploadDTO uploadDTO, Long assignmentSolutionId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution " + assignmentSolutionId + " does not exist"));
    StudentUpload studentUpload = modelMapper.map(uploadDTO, StudentUpload.class);
    studentUpload.setTimestamp(new Timestamp(System.currentTimeMillis()));
    assignmentSolution.getStudentUploads().add(studentUpload);
    assignmentSolution.setStatus(AssignmentStatus.DELIVERED);
    return modelMapper.map(studentUploadRepository.save(studentUpload), UploadDTO.class);
  }

  @Override
  public Resource getAssignmentForStudent(Long assignmentId, String studentId) throws FileNotFoundException {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findByAssignment_IdAndStudent_Id(
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
  public UploadDTO uploadProfessorUpload(UploadDTO uploadDTO, Long studentUploadId, boolean reUploadable) {
    StudentUpload studentUpload = studentUploadRepository.findById(studentUploadId)
        .orElseThrow(() -> new StudentUploadNotFoundException("Student upload " + studentUploadId + " does not exist"));
    ProfessorUpload professorUpload = modelMapper.map(uploadDTO, ProfessorUpload.class);
    professorUpload.setTimestamp(new Timestamp(System.currentTimeMillis()));
    studentUpload.setTeacherRevision(professorUpload);
    professorUpload.setRevisedSolution(studentUpload);
    if (reUploadable)
      studentUpload.getAssignmentSolution().setStatus(AssignmentStatus.REVIEWED_UPLOADABLE);
    else
      studentUpload.getAssignmentSolution().setStatus(AssignmentStatus.REVIEWED);
    return modelMapper.map(professorUploadRepository.save(professorUpload), UploadDTO.class);
  }

  @Override
  public void assignGrade(Long assignmentSolutionId, String grade) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId)
        .orElseThrow(() -> new AssignmentSolutionNotFoundException(
            "Assignment solution " + assignmentSolutionId + " does not exist"));
    if (assignmentSolution.getStatus().equals(AssignmentStatus.DEFINITIVE))
      throw new DefinitiveAssignmentSolutionStatusException(
          "Assignment solution " + assignmentSolutionId + " status already definitive");
    if (!assignmentSolution.getStatus().equals(AssignmentStatus.REVIEWED))
      throw new AssignmentSolutionNotReviewedException(
          "Assignment solution " + assignmentSolutionId + " status already definitive");
    assignmentSolution.setGrade(grade);
    assignmentSolution.setStatus(AssignmentStatus.DEFINITIVE);
  }
}
