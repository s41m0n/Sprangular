package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.exceptions.CourseNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.pojos.TeamProposalDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  NotificationService notificationService;

  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
  public Optional<StudentDTO> getStudent(String studentId) {
    return studentRepository.findById(studentId)
        .map(student -> modelMapper.map(student, StudentDTO.class));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public List<StudentDTO> getAllStudents() {
    return studentRepository.findAll().stream()
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
  public List<CourseDTO> getStudentCourses(String studentId) {
    return studentRepository.findById(studentId)
        .map(student -> student.getCourses().stream()
            .map(course -> modelMapper.map(course, CourseDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') " +
      "and @securityServiceImpl.isStudentSelf(#studentId) and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseId) {
    studentRepository.findById(studentId).orElseThrow(
        () -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    Course course = courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));

    List<Proposal> proposals = proposalRepository.findAllByInvitedUserIdAndCourseId(studentId, course.getAcronym());

    if (proposals.isEmpty()) {
      return null;
    }

    List<TeamProposalDetails> proposalsDetails = new ArrayList<>();

    for (Proposal p : proposals) {

      if (Utility.isProposalDeleted(p.getStatus())) {
        continue;
      }

      TeamProposalDetails tpd = new TeamProposalDetails();
      tpd.setTeamName(teamRepository.getOne(p.getTeamId()).getName());
      Student creator = studentRepository.getOne(p.getProposalCreatorId());
      tpd.setProposalCreator(creator.getName() + " " + creator.getSurname() + " (" + creator.getId() + ")");
      tpd.setToken(p.getId());

      List<String> teamApprovalDetails = new ArrayList<>();

      for (Proposal pr : proposalRepository.findAllByTeamId(p.getTeamId())) {
        Student s = studentRepository.getOne(pr.getInvitedUserId());
        if (Utility.isProposalDeleted(pr.getStatus())) {
          teamApprovalDetails.add(s.getName() + " " + s.getSurname() + " (" + s.getId() + ") : "
              + Utility.proposalStatusString(pr.getStatus()));
        } else {
          teamApprovalDetails.add(s.getName() + " " + s.getSurname() + " (" + s.getId() + ") : "
              + pr.getStatus().name());
        }
      }

      tpd.setMembersAndStatus(teamApprovalDetails);
      tpd.setDeadline(p.getDeadline());
      tpd.setValid(p.isValid());

      proposalsDetails.add(tpd);
    }

    return proposalsDetails;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR')")
  public List<StudentDTO> getStudentsLike(String pattern) {
    List<StudentDTO> returnedList = new ArrayList<>();
    for (StudentDTO s : this.getAllStudents()) {
      if (s.getSurname().toLowerCase().contains(pattern.toLowerCase())) {
        returnedList.add(s);
      }
    }
    return returnedList;
  }
}
