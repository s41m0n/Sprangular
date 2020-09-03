package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.exceptions.CourseNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotInCourseException;
import it.polito.ai.lab2.repositories.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
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
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> modelMapper.map(student, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public boolean addStudentToCourse(String studentId, String courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) throw new StudentNotFoundException("Student " + studentId + " does not exist");

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) throw new CourseNotFoundException("Course " + courseId + " does not exist");

        if (student.getCourses().contains(course) || !course.isEnabled()) return false;
        student.addCourse(course);
        return true;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public List<Boolean> enrollAll(List<String> studentIds, String courseId) {
        return studentIds.stream()
                .map(studentId -> addStudentToCourse(studentId, courseId))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public List<Boolean> addAndEnroll(Reader r, String courseId) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();

        this.addAll(students);

        return enrollAll(students.stream().map(StudentDTO::getId).collect(Collectors.toList()), courseId);
    }

    @Override
    public List<CourseDTO> getStudentCourses(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getCourses().stream()
                        .map(course -> modelMapper.map(course, CourseDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    }

    @Override
    public boolean removeStudentFromCourse(String studentId, String courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        if(!course.getStudents().contains(student)){
            throw new StudentNotInCourseException("Some student is not enrolled in course " + courseId);
        }

        student.removeCourse(course); //symmetric method, it updates also the course
        return true;
    }

    private String getPredefinedRegisterMessage(String id, String pwd) {
        return "Welcome to SpringExample app!\n\n" +
                "Your access credentials are:\n" +
                "-Username: " + id +
                "\n-Password: " + pwd + "" +
                "\n\nAuthenticate through http://localhost:8080/API/authenticate and enjoy!";
    }
}
