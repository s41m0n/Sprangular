package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.CourseWithModelDetails;
import it.polito.ai.lab2.pojos.StudentWithTeamDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.AssignmentStatus;
import it.polito.ai.lab2.utility.ProposalStatus;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  VmModelRepository vmModelRepository;

  @Autowired
  StudentService studentService;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  TeamService teamService;

  @Autowired
  AssignmentSolutionRepository assignmentSolutionRepository;

  @Autowired
  UploadRepository uploadRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  AssignmentRepository assignmentRepository;

  @Autowired
  VmRepository vmRepository;

  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  NotificationService notificationService;

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public CourseDTO addCourse(CourseWithModelDetails course) {
    if (courseRepository.existsById(course.getAcronym())) {
      throw new DuplicatedCourseException("Course " + course.getAcronym() + " already exists");
    }

    if (course.getTeamMaxSize() < course.getTeamMinSize()
        || course.getTeamMaxSize() < 0
        || course.getTeamMinSize() < 0) {
      throw new TeamSizesNotCoherentException("Cannot create course " + course.getAcronym() + ": wrong team sizes");
    }

    Course c = modelMapper.map(course, Course.class);

    VmModel v = new VmModel();
    v.setName(c.getAcronym());
    v.assignToCourse(c);

    VmModel savedVmModel = vmModelRepository.save(v);

    Path vmModelPath = Utility.VM_MODELS_DIR.resolve(savedVmModel.getId().toString());
    savedVmModel.setImagePath(vmModelPath.toString());
    try {
      Files.copy(course.getVmModel().getInputStream(), vmModelPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    courseRepository.save(c);
    if (professorRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).isPresent()) {
      this.addProfessorToCourse(professorRepository.getOne(
          SecurityContextHolder.getContext().getAuthentication().getName()).getId(), course.getAcronym());
    }
    return modelMapper.map(c, CourseDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public Optional<CourseDTO> getCourse(String courseId) {
    return courseRepository.findById(courseId)
        .map(course -> modelMapper.map(course, CourseDTO.class));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public List<CourseDTO> getAllCourses() {
    return courseRepository.findAll().stream()
        .map(course -> modelMapper.map(course, CourseDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("(hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) " +
      "or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId))")
  public List<StudentWithTeamDetails> getEnrolledStudents(String courseId) {
    List<StudentDTO> students = courseRepository.findById(courseId)
        .map(course -> course.getStudents().stream()
            .map(student -> modelMapper.map(student, StudentDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

    List<StudentWithTeamDetails> returnedList = new ArrayList<>();

    for (StudentDTO s : students) {
      modelMapper.map(s, StudentWithTeamDetails.class).setTeam(
          modelMapper.map(teamService.getTeamOfStudentOfCourse(s.getId(), courseId), TeamDTO.class));
    }

    return returnedList;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public boolean enableCourse(String courseId) {
    courseRepository.findById(courseId)
        .ifPresentOrElse(course -> {
          if (course.getProfessors().isEmpty())
            throw new CourseProfessorNotAssignedException(
                "You can enable course " + courseId + " only when at least one professor has been assigned to it");
          course.setEnabled(true);
        }, () -> {
          throw new CourseNotFoundException("Course " + courseId + " does not exist");
        });
    return true;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public boolean disableCourse(String courseId) {
    courseRepository.findById(courseId)
        .ifPresentOrElse(course -> course.setEnabled(false), () -> {
          throw new CourseNotFoundException("Course " + courseId + " does not exist");
        });
    return false;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public List<ProfessorDTO> getCourseProfessors(String courseId) {
    Course c = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
    return c.getProfessors() == null ? null
        : c.getProfessors().stream().map(p -> modelMapper.map(p, ProfessorDTO.class)).collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public boolean addProfessorToCourse(String professor, String courseId) {
    Course c = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
    Professor p = professorRepository.findById(professor).orElseThrow(
        () -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
    if (p.isVerified()) {
      return p.addCourse(c);
    }
    throw new UserNotVerifiedException("Professor " + professor + " is not verified");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public ProfessorDTO removeProfessorFromCourse(String professor, String courseId) {
    Course c = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
    Professor p = professorRepository.findById(professor).orElseThrow(
        () -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
    if (c.getProfessors().size() > 1) {
      p.removeCourse(c);
      return modelMapper.map(p, ProfessorDTO.class);
    }
    throw new CourseWithoutProfessorException(
        "Cannot remove professor " + professor + " from course " + courseId + ", it is the only professor");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public List<StudentWithTeamDetails> getStudentsOfCourse(String courseId, String pattern) {
    boolean applyFilter = pattern != null && !pattern.isEmpty();
    return courseRepository.findById(courseId)
        .map(course -> course.getStudents().stream()
            .filter(x -> !applyFilter || x.getSurname().toLowerCase().contains(pattern.toLowerCase()))
            .map(x -> {
              if (applyFilter) {
                return modelMapper.map(x, StudentWithTeamDetails.class);
              }
              StudentWithTeamDetails ret = modelMapper.map(x, StudentWithTeamDetails.class);
              x.getTeams().stream()
                  .filter(y -> y.isActive() && y.getCourse().getAcronym().equals(courseId)).findAny()
                  .ifPresent(team -> ret.setTeam(modelMapper.map(team, TeamDTO.class)));
              return ret;
            })
            .collect(Collectors.toList()))
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public CourseDTO removeCourse(String courseId) {
    Course c = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
    // Delete all the associated resources too, gathering all the images paths in this list to delete them once
    List<Path> imagesToDelete = new ArrayList<>();
    imagesToDelete.add(Utility.VM_MODELS_DIR.resolve(c.getVmModel().getId().toString()));
    vmModelRepository.delete(c.getVmModel());
    proposalRepository.deleteAllByCourseId(courseId);
    c.getTeams().forEach(t -> {
      t.getVms().forEach(v -> imagesToDelete.add(Utility.VMS_DIR.resolve(v.getId().toString())));
      vmRepository.deleteAll(t.getVms());
    });
    c.getAssignments().forEach(a -> {
      imagesToDelete.add(Utility.ASSIGNMENTS_DIR.resolve(a.getId().toString()));
      a.getSolutions().forEach(s -> {
        s.getUploads().stream()
            .filter(u -> u.getImagePath() != null)
            .forEach(u -> imagesToDelete.add(Utility.UPLOADS_DIR.resolve(u.getId().toString())));
        uploadRepository.deleteAll(s.getUploads());
      });
      assignmentSolutionRepository.deleteAll(a.getSolutions());
    });
    teamRepository.deleteAll(c.getTeams());
    assignmentRepository.deleteAll(c.getAssignments());
    courseRepository.delete(c);
    imagesToDelete.forEach(path -> {
      try {
        Files.delete(path);
      } catch (IOException e) {
        throw new RuntimeException("Cannot delete the file: " + e.getMessage());
      }
    });
    
    return modelMapper.map(c, CourseDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public CourseDTO updateCourse(String courseId, CourseWithModelDetails course) {

    if (course.getTeamMaxSize() < course.getTeamMinSize()
        || course.getTeamMaxSize() < 0
        || course.getTeamMinSize() < 0) {
      throw new CannotUpdateCourseException("Cannot update course " + course.getAcronym() + ": wrong team sizes");
    }

    Course c = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));

    for (Team t : c.getTeams()) {
      if (t.getMembers().size() < course.getTeamMinSize()
          || t.getMembers().size() > course.getTeamMaxSize()) {
        throw new CannotUpdateCourseException(
            "Course " + courseId + " cannot be updated: some teams are not compliant with new restrictions");
      }
    }

    if(c.getVmModel() != null && course.getVmModel() != null) {
      Path oldVmModelPath = Utility.VM_MODELS_DIR.resolve(c.getVmModel().getId().toString());
      try {
        Files.delete(oldVmModelPath);
      } catch (IOException e) {
        throw new RuntimeException("Cannot delete the file: " + e.getMessage());
      }
      vmModelRepository.delete(c.getVmModel());
    }

    if(course.getVmModel() != null) {
      VmModel v = new VmModel();
      v.setName(c.getAcronym());
      v.assignToCourse(c);

      VmModel savedVmModel = vmModelRepository.save(v);

      Path vmModelPath = Utility.VM_MODELS_DIR.resolve(savedVmModel.getId().toString());
      savedVmModel.setImagePath(vmModelPath.toString());
      try {
        Files.copy(course.getVmModel().getInputStream(), vmModelPath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException("Cannot store the file: " + e.getMessage());
      }
    }

    c.setTeamMaxSize(course.getTeamMaxSize());
    c.setTeamMinSize(course.getTeamMinSize());
    c.setEnabled(course.isEnabled());
    courseRepository.save(c);

    return modelMapper.map(c, CourseDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public boolean addStudentToCourse(String studentId, String courseId) {
    Student student = studentRepository.findById(studentId).orElse(null);
    if (student == null) throw new StudentNotFoundException("Student " + studentId + " does not exist");

    Course course = courseRepository.findById(courseId).orElse(null);
    if (course == null) throw new CourseNotFoundException("Course " + courseId + " does not exist");

    if (!course.isEnabled()) {
      throw new CourseNotEnabledException("Course " + courseId + " is not enabled");
    }

    if (student.getCourses().contains(course)) {
      throw new StudentAlreadyInCourseException("Student " + studentId + " already enrolled in course " + courseId);
    }

    if (student.isVerified()) {
      student.addCourse(course);
      Timestamp currentTs = new Timestamp(System.currentTimeMillis());
      course.getAssignments().stream()
          .filter(a -> a.getDueDate().after(currentTs))
          .forEach(a -> {
            AssignmentSolution assignmentSolution = new AssignmentSolution();
            assignmentSolution.setAssignment(a);
            a.getSolutions().add(assignmentSolution);
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
      return true;
    }
    throw new UserNotVerifiedException("Student " + studentId + " is not verified");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public List<Boolean> enrollAll(Reader r, String courseId) {
    courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));

    CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
        .withType(StudentDTO.class)
        .withIgnoreLeadingWhiteSpace(true)
        .build();

    List<StudentDTO> students = csvToBean.parse();

    for (StudentDTO s : students) {
      Student student = studentRepository.findById(s.getId()).orElseThrow(
          () -> new StudentNotFoundException("Student " + s.getId() + " does not exist"));
      if (!student.isVerified()) {
        throw new UserNotVerifiedException("Student " + s.getId() + " is not verified");
      }
    }

    return students.stream()
        .map(studentId -> addStudentToCourse(studentId.getId(), courseId))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public StudentDTO removeStudentFromCourse(String studentId, String courseId) {
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    Course course = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));

      if (!course.getStudents().contains(student)) {
        throw new StudentNotInCourseException("Student " + studentId + " is not enrolled in course " + courseId);
      }
      if (student.getTeams().stream().anyMatch(t -> t.getCourse().getAcronym().equals(courseId) && t.isActive())) {
        throw new StudentAlreadyInTeamException("Student " + studentId + " is a member of a team");
      }
    proposalRepository.findAllByInvitedUserIdAndCourseId(studentId, courseId)
        .forEach(p -> {
          if (p.getStatus().equals(ProposalStatus.PENDING)) {
            notificationService.reject(p.getId());
          }
          notificationService.deleteProposal(p.getId());
        });
      student.removeCourse(course); //symmetric method, it updates also the course

    return modelMapper.map(student, StudentDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public List<StudentDTO> removeStudentsFromCourse(List<String> studentIds, String courseId) {
    return studentIds.stream()
        .map(studentId -> removeStudentFromCourse(studentId, courseId))
        .collect(Collectors.toList());
  }
}
