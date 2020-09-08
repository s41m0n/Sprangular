package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.exceptions.CourseNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.pojos.TeamProposalDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.ProposalStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public boolean addStudent(StudentDTO student) {
        if (userRepository.findById(student.getId()).isPresent()) return false;

        Role role = roleRepository.findByName("ROLE_STUDENT").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_STUDENT");
            return r;
        });

        Student s = modelMapper.map(student, Student.class);
        String pwd = RandomStringUtils.random(10, true, true);
        s.setPassword(passwordEncoder.encode(pwd));
        s.getRoles().add(role);
        s.setVerified(false);
        userRepository.save(s);
        notificationService.sendMessage("s" + student.getId() + "@studenti.polito.it", "Account Creation", getPredefinedRegisterMessage(student.getId(), pwd));
        return true;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> modelMapper.map(student, StudentDTO.class));
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId)")
    public List<CourseDTO> getStudentCourses(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getCourses().stream()
                        .map(course -> modelMapper.map(course, CourseDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId) and @securityServiceImpl.isStudentEnrolled(#courseId)")
    public List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseId) {
        studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        List<Proposal> proposals = proposalRepository.findAllByInvitedUserIdAndCourseId(studentId, course.getAcronym());

        if (proposals.isEmpty()) {
            return null;
        }

        List<TeamProposalDetails> proposalsDetails = new ArrayList<>();

        for (Proposal p : proposals) {
            TeamProposalDetails tpd = new TeamProposalDetails();
            tpd.setTeamName(teamRepository.getOne(p.getTeamId()).getName());
            tpd.setProposalCreator(studentRepository.getOne(p.getProposalCreatorId()));

            Map<Student, ProposalStatus> teamApprovalDetails = new HashMap<>();

            for (Proposal pr : proposalRepository.findAllByTeamId(p.getTeamId())) {
                teamApprovalDetails.put(studentRepository.getOne(pr.getInvitedUserId()), pr.getStatus());
            }

            tpd.setMembersAndStatus(teamApprovalDetails);

            proposalsDetails.add(tpd);
        }

        return proposalsDetails;
    }

    private String getPredefinedRegisterMessage(String id, String pwd) {
        return "Welcome to SpringExample app!\n\n" +
                "Your access credentials are:\n" +
                "-Username: " + id +
                "\n-Password: " + pwd + "" +
                "\n\nAuthenticate through http://localhost:8080/API/authenticate and enjoy!";
    }
}
