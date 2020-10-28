package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.exceptions.ProfessorNotFoundException;
import it.polito.ai.lab2.repositories.ProfessorRepository;
import it.polito.ai.lab2.repositories.RoleRepository;
import it.polito.ai.lab2.repositories.UserRepository;
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
public class ProfessorServiceImpl implements ProfessorService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  NotificationService notificationService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  public List<ProfessorDTO> getProfessors() {
    return professorRepository.findAll().stream()
        .map(professor -> modelMapper.map(professor, ProfessorDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  public List<ProfessorDTO> getProfessorsLike(String pattern) {
    List<ProfessorDTO> returnedList = new ArrayList<>();
    for (ProfessorDTO p : this.getProfessors()) {
      if (p.getSurname().toLowerCase().contains(pattern.toLowerCase())) {
        returnedList.add(p);
      }
    }
    return returnedList;
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  public Optional<ProfessorDTO> getProfessor(String id) {
    return professorRepository.findById(id)
        .map(professor -> modelMapper.map(professor, ProfessorDTO.class));
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  public List<CourseDTO> getProfessorCourses(String id) {
    return professorRepository.findById(id)
        .map(p -> p.getCourses().stream()
            .map(course -> modelMapper.map(course, CourseDTO.class))
            .collect(Collectors.toList()))
        .orElseThrow(() -> new ProfessorNotFoundException("Professor `" + id + "` does not exist"));
  }
}
