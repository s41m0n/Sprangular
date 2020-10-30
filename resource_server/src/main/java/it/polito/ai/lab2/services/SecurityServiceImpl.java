package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  VmRepository vmRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  AssignmentRepository assignmentRepository;

  @Autowired
  StudentUploadRepository studentUploadRepository;

  @Autowired
  AssignmentSolutionRepository assignmentSolutionRepository;

  @Override
  public boolean isStudentSelf(String id) {
    return SecurityContextHolder.getContext().getAuthentication().getName().equals(id);
  }

  @Override
  public boolean isTeamOfStudentCourse(Long id) {
    return studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
        .map(student -> student.getCourses().stream()
            .anyMatch(course -> course.getTeams().stream()
                .anyMatch(team -> team.getId().equals(id))))
        .orElse(false);
  }

  @Override
  public boolean isProfessorCourseOwner(String courseId) {

    if (!courseRepository.existsById(courseId) || courseRepository.getOne(courseId).getProfessors().isEmpty()) {
      return false;
    }

    List<String> professorsIds = new ArrayList<>();

    for (Professor p : courseRepository.getOne(courseId).getProfessors()) {
      professorsIds.add(p.getId());
    }

    return professorsIds.contains(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Override
  public boolean isStudentEnrolled(String courseId) {
    return studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
        .map(student -> student.getCourses().stream().anyMatch(c -> c.getAcronym().equals(courseId)))
        .orElse(false);
  }

  @Override
  public boolean isTeamOfProfessorCourse(Long id) {
    return professorRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
        .map(professor -> professor.getCourses().stream()
            .anyMatch(course -> course.getTeams().stream()
                .anyMatch(team -> team.getId().equals(id))))
        .orElse(false);
  }

  @Override
  public boolean isStudentInTeamRequest(List<String> memberIds) {
    return memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Override
  public boolean isStudentOwnerOfVm(Long vmId) {
    Vm vm = vmRepository.findById(vmId).orElse(null);
    if (vm == null)
      return false;
    return vm.getOwners().stream()
        .anyMatch(s -> s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isVmOfStudentTeam(Long vmId) {
    Vm vm = vmRepository.findById(vmId).orElse(null);
    if (vm == null)
      return false;
    return vm.getTeam().getMembers().stream()
        .anyMatch(s -> s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isStudentInTeam(Long teamId) {
    Team team = teamRepository.findById(teamId).orElse(null);
    if (team == null)
      return false;
    return team.getMembers().stream()
        .anyMatch(s -> s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isAssignmentOfProfessorCourse(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
    if (assignment == null)
      return false;
    return assignment.getCourse().getProfessors().stream()
        .anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isAssignmentOfProfessor(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
    if (assignment == null)
      return false;
    return assignment.getProfessor().getId().equals(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Override
  public boolean hasStudentTheAssignment(Long assignmentId) {
    Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
    if (assignment == null)
      return false;
    return assignment.getCourse().getStudents().stream()
        .anyMatch(s -> s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isProfessorUploadReviewer(Long studentUploadId) {
    StudentUpload studentUpload = studentUploadRepository.findById(studentUploadId).orElse(null);
    if (studentUpload == null)
      return false;
    return studentUpload.getAssignmentSolution().getAssignment().getProfessor().getId()
        .equals(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Override
  public boolean isAssignmentSolutionOfProfessorCourse(Long assignmentSolutionId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId).orElse(null);
    if (assignmentSolution == null)
      return false;
    return assignmentSolution.getAssignment().getCourse().getProfessors().stream()
        .anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @Override
  public boolean isAssignmentSolutionOfStudent(Long assignmentSolutionId) {
    AssignmentSolution assignmentSolution = assignmentSolutionRepository.findById(assignmentSolutionId).orElse(null);
    if (assignmentSolution == null)
      return false;
    return assignmentSolution.getStudent().getId().equals(
        SecurityContextHolder.getContext().getAuthentication().getName());
  }
}
