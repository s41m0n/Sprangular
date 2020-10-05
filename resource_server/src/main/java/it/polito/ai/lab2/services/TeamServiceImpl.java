package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.SetVmsResourceLimits;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.ProposalStatus;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Log
public class TeamServiceImpl implements TeamService {

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  NotificationService notificationService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  TaskScheduler scheduler;

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId))")
  public List<TeamDTO> getTeamsForStudent(String studentId) {
    return studentRepository.findById(studentId)
        .map(student -> student.getTeams().stream()
            .map(team -> modelMapper.map(team, TeamDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId))")
  public TeamDTO getTeamOfStudentOfCourse(String studentId, String courseId) {
    return modelMapper.map(studentRepository.findById(studentId)
        .map(student -> student.getTeams().stream()
            .filter(x -> x.getCourse().getAcronym().equals(courseId))
            .findFirst()
            .orElseThrow(() -> new TeamNotFoundException("No team for student" + studentId + " in course " + courseId))).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist")), TeamDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#teamId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#teamId))")
  public List<StudentDTO> getTeamMembers(Long teamId) {
    return teamRepository.findById(teamId)
        .map(team -> team.getMembers().stream()
            .map(student -> modelMapper.map(student, StudentDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId) and @securityServiceImpl.isStudentInTeamRequest(#memberIds)")
  public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, LocalDate deadline) {
    if (LocalDate.now().isBefore(deadline))
      throw new InvalidTimestampException("Timestamp before current date");

    if (memberIds.stream().distinct().count() != memberIds.size())
      throw new DuplicateStudentInTeam("Some student is already in the group " + name);

    Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

    if (!course.isEnabled()) throw new CourseNotEnabledException("Course " + courseId + " is not enabled");

    if (course.getTeams().stream().anyMatch(x -> x.getName().equals(name)))
      throw new TeamNameAlreadyInCourseException("Team `" + name + "` already in course `" + courseId + "`");

    if (memberIds.size() > course.getTeamMaxSize() || memberIds.size() < course.getTeamMinSize())
      throw new IllegalTeamMemberException("For the course " + courseId + " team must be composed between " + course.getTeamMinSize() + " and " + course.getTeamMaxSize() + " students");

    List<Student> members = studentRepository.findAllById(memberIds);

    if (members.stream().anyMatch(User::isVerified)) {
      throw new UserNotVerifiedException("Some student's account is not verified");
    }

    if (members.size() != memberIds.size())
      throw new StudentNotFoundException("Some student does not exist in the database");

    if (!course.getStudents().containsAll(members))
      throw new StudentNotInCourseException("Some student is not enrolled in course " + courseId);

    if (members.stream()
        .anyMatch(student -> student.getTeams().stream()
            .anyMatch(team -> team.getCourse().getAcronym().equals(courseId))))
      throw new StudentAlreadyInTeam("Some student is already in a team for the course " + courseId);

    Student creator = members.stream()
        .filter(s -> s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
        .findFirst().orElseThrow(() -> new StudentNotFoundException("Creator not in members"));

    boolean isAlone = memberIds.size() == 1;
    Team team = new Team();
    team.setMembers(members);
    team.setCourse(course);
    team.setName(name);
    team.setActive(isAlone);
    TeamDTO t = modelMapper.map(teamRepository.save(team), TeamDTO.class);
    if (!isAlone) {
      members.forEach(
          member -> {
            Proposal proposal = new Proposal();
            proposal.setId(UUID.randomUUID().toString());
            proposal.setProposalCreatorId(creator.getId());
            proposal.setInvitedUserId(member.getId());
            proposal.setTeamId(t.getId());
            proposal.setCourseId(courseId);
            proposal.setDeadline(deadline);
            proposal.setStatus(ProposalStatus.PENDING);
            proposalRepository.save(proposal);
          }
      );
      Runnable proposalDeadline = () -> {
        log.info("Deadline for proposal ");
        List<Proposal> proposals = proposalRepository.findAllByTeamId(t.getId());
        boolean toDelete = proposals.stream()
            .anyMatch(p -> p.getStatus().equals(ProposalStatus.REJECTED)
                || p.getStatus().equals(ProposalStatus.PENDING));
        if (toDelete) {
          proposals.forEach(proposal -> proposal.setStatus(ProposalStatus.REJECTED));
          teamRepository.deleteById(t.getId());
        }
      };
      //scheduler.schedule(proposalDeadline, new CronTrigger(Utility.timestampToCronTrigger(deadline)));
      scheduler.schedule(proposalDeadline, Instant.from(deadline)); //TODO: testare!
      notificationService.notifyTeam(t, memberIds, courseId);
    }
    return t;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId))")
  public List<TeamDTO> getTeamsForCourse(String courseId) {
    return courseRepository.findById(courseId)
        .map(course -> course.getTeams().stream()
            .map(team -> modelMapper.map(team, TeamDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId))")
  public List<StudentDTO> getStudentsInTeams(String courseId) {
    return courseRepository.getStudentsInTeams(courseId).stream()
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId))")
  public List<StudentDTO> getAvailableStudents(String courseId) {
    return courseRepository.getStudentsNotInTeams(courseId).stream()
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getAvailableStudentsLike(String courseId, String pattern) {
    return courseRepository.getStudentsNotInTeams(courseId).stream()
        .map(student -> modelMapper.map(student, StudentDTO.class))
        .filter(s -> s.getSurname().toLowerCase().contains(pattern.toLowerCase()))
        .collect(Collectors.toList());
  }

  @Override
  public void activateTeam(Long id) {
    Team team = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException("Team + " + id + " does not exist"));
    team.setActive(true);
    teamRepository.save(team);
  }

  @Override
  public void evictTeam(Long id) {
    teamRepository.deleteById(id);
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  public List<TeamDTO> getTeams() {
    return teamRepository.findAll().stream()
        .map(t -> modelMapper.map(t, TeamDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#id)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#id))")
  public Optional<TeamDTO> getTeam(Long id) {
    return teamRepository.findById(id)
        .map(t -> modelMapper.map(t, TeamDTO.class));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#id)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#id))")
  public CourseDTO getCourseForTeam(Long id) {
    return teamRepository.findById(id)
        .map(team -> modelMapper.map(team.getCourse(), CourseDTO.class))
        .orElseThrow(() -> new TeamNotFoundException("Team `" + id + "` does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#teamId)")
  public TeamDTO setVmsResourceLimits(Long teamId, SetVmsResourceLimits vmResourceLimits) {
    Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));

    int actualVCpu = 0;
    int actualRam = 0;
    int actualDiskStorage = 0;
    int numOfActiveVms = 0;

    for (Vm vm : team.getVms()) {
      actualVCpu += vm.getVCpu();
      actualRam += vm.getRam();
      actualDiskStorage += vm.getDiskStorage();
      if (vm.isActive()) {
        numOfActiveVms += 1;
      }
    }

    if (actualVCpu > vmResourceLimits.getVCpu() || actualRam > vmResourceLimits.getRam() || actualDiskStorage > vmResourceLimits.getDiskStorage() || numOfActiveVms > vmResourceLimits.getMaxActiveInstances() || team.getVms().size() > vmResourceLimits.getMaxTotalInstances()) {
      throw new TooManyActualResourcesException("Cannot set VMs resource limits, actual used resources are higher than the new limits");
    }

    team.setMaxVCpu(vmResourceLimits.getVCpu());
    team.setMaxRam(vmResourceLimits.getRam());
    team.setMaxDiskStorage(vmResourceLimits.getDiskStorage());
    team.setMaxActiveInstances(vmResourceLimits.getMaxActiveInstances());
    team.setMaxTotalInstances(vmResourceLimits.getMaxTotalInstances());
    teamRepository.save(team);
    return modelMapper.map(team, TeamDTO.class);
  }
}
