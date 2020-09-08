package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.entities.Professor;
import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.exceptions.ProfessorNotFoundException;
import it.polito.ai.lab2.repositories.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfessorServiceImpl implements ProfessorService{

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean addProfessor(ProfessorDTO professorDTO) {
        if (userRepository.findById(professorDTO.getId()).isPresent()) return false;

        Role role = roleRepository.findByName("ROLE_PROFESSOR").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_PROFESSOR");
            return r;
        });

        Professor p = modelMapper.map(professorDTO, Professor.class);
        String pwd = RandomStringUtils.random(10, true, true);
        p.setPassword(passwordEncoder.encode(pwd));
        p.addRole(role);
        p.setVerified(false);
        userRepository.save(p);
        notificationService.sendMessage("p" + professorDTO.getId() + "@polito.it", "Account Creation", getPredefinedRegisterMessage(professorDTO.getId(), pwd));
        return true;
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

    private String getPredefinedRegisterMessage(String id, String pwd) {
        return "Welcome to SpringExample app!\n\n" +
                "Your access credentials are:\n" +
                "-Username: " + id +
                "\n-Password: " + pwd + "" +
                "\n\nAuthenticate through http://localhost:8080/API/authenticate and enjoy!";
    }
}
