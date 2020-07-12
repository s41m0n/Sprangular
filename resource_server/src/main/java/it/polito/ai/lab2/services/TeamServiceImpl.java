package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean addCourse(CourseDTO course) {
        if (course.getMax() < course.getMin() || courseRepository.findById(course.getName()).isPresent())
            return false;
        Course c = modelMapper.map(course, Course.class);
        courseRepository.save(c);
        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepository.findById(name)
                .map(course -> modelMapper.map(course, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

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
        s.addRole(role);
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        return courseRepository.findById(courseName)
                .map(course -> course.getStudents().stream()
                        .map(student -> modelMapper.map(student, StudentDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public boolean addStudentToCourse(String studentId, String courseName) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) throw new StudentNotFoundException("Student " + studentId + " does not exist");

        Course course = courseRepository.findById(courseName).orElse(null);
        if (course == null) throw new CourseNotFoundException("Course " + courseName + " does not exist");

        if (student.getCourses().contains(course) || !course.isEnabled()) return false;
        student.addCourse(course);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public void enableCourse(String courseName) {
        courseRepository.findById(courseName)
                .ifPresentOrElse(course -> {
                    if (course.getProfessor() == null)
                        throw new CourseProfessorNotAssigned("You can enable cuourse `" + courseName + "` only when a professor has been assigned to it");
                    course.setEnabled(true);
                }, () -> {
                    throw new CourseNotFoundException("Course " + courseName + " does not exist");
                });
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public void disableCourse(String courseName) {
        courseRepository.findById(courseName)
                .ifPresentOrElse(course -> course.setEnabled(false), () -> {
                    throw new CourseNotFoundException("Course " + courseName + " does not exist");
                });
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        return studentIds.stream()
                .map(studentId -> addStudentToCourse(studentId, courseName))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();

        addAll(students);

        return enrollAll(students.stream().map(StudentDTO::getId).collect(Collectors.toList()), courseName);
    }

    @Override
    public List<CourseDTO> getCourses(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getCourses().stream()
                        .map(course -> modelMapper.map(course, CourseDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    }

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
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#teamId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#teamId))")
    public List<StudentDTO> getMembers(Long teamId) {
        return teamRepository.findById(teamId)
                .map(team -> team.getMembers().stream()
                        .map(student -> modelMapper.map(student, StudentDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId) and @securityServiceImpl.isStudentInTeamRequest(#memberIds)")
    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds) {
        if (memberIds.stream().distinct().count() != memberIds.size())
            throw new DuplicateStudentInTeam("Some student is already in the group " + name);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        if (!course.isEnabled()) throw new CourseNotEnabledException("Course " + courseId + " is not enabled");


        if (course.getTeams().stream().anyMatch(x -> x.getName().equals(name)))
            throw new TeamNameAlreadyInCourseException("Team `" + name + "` already in course `" + courseId + "`");

        if (memberIds.size() > course.getMax() || memberIds.size() < course.getMin())
            throw new IllegalTeamMemberException("For the course " + courseId + " team must be composed between " + course.getMin() + " and " + course.getMax() + " students");

        List<Student> members = studentRepository.findAllById(memberIds);

        if (members.size() != memberIds.size())
            throw new StudentNotFoundException("Some student does not exist in the database");

        if (!course.getStudents().containsAll(members))
            throw new StudentNotInCourseException("Some student is not enrolled in course " + courseId);

        if (members.stream()
                .anyMatch(student -> student.getTeams().stream()
                        .anyMatch(team -> team.getCourse().getName().equals(courseId))))
            throw new StudentAlreadyInTeam("Some student is already in a team for the course " + courseId);

        boolean isAlone = memberIds.size() == 1;
        Team team = new Team();
        team.setMembers(members);
        team.setCourse(course);
        team.setName(name);
        team.setStatus(isAlone ? 1 : 0);
        TeamDTO t = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        if (!isAlone) notificationService.notifyTeam(t, memberIds);
        return t;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<TeamDTO> getTeamForCourse(String courseName) {
        return courseRepository.findById(courseName)
                .map(course -> course.getTeams().stream()
                        .map(team -> modelMapper.map(team, TeamDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        return courseRepository.getStudentsInTeams(courseName).stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getAvailableStudents(String courseName) {
        return courseRepository.getStudentsNotInTeams(courseName).stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void activeTeam(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException("Team + " + id + " does not exist"));
        team.setStatus(1);
    }

    @Override
    public void evictTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
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
        userRepository.save(p);
        notificationService.sendMessage("p" + professorDTO.getId() + "@polito.it", "Account Creation", getPredefinedRegisterMessage(professorDTO.getId(), pwd));
        return true;
    }

    @Override
    public ProfessorDTO getCourseProfessor(String name) {
        Course c = courseRepository.findById(name).orElseThrow(() -> new CourseNotFoundException("Course `" + name + "` does not exist"));
        return c.getProfessor() == null ? null : modelMapper.map(c.getProfessor(), ProfessorDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean setProfessorForCourse(String professor, String name) {
        Course c = courseRepository.findById(name).orElseThrow(() -> new CourseNotFoundException("Course `" + name + "` does not exist"));
        Professor p = professor.isEmpty() ? null : professorRepository.findById(professor).orElseThrow(() -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
        if (c.getProfessor() != null) c.getProfessor().removeCourse(c);
        if (p != null) p.addCourse(c);
        return true;
    }

    @Override
    public List<CourseDTO> getProfessorCourses(String id) {
        return professorRepository.findById(id)
                .map(p -> p.getProfessorCourses().stream()
                        .map(course -> modelMapper.map(course, CourseDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ProfessorNotFoundException("Professor `" + id + "` does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    public Optional<ProfessorDTO> getProfessor(String id) {
        return professorRepository.findById(id)
                .map(professor -> modelMapper.map(professor, ProfessorDTO.class));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#id)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#id))")
    public CourseDTO getCourseForTeam(Long id) {
        return teamRepository.findById(id)
                .map(team -> modelMapper.map(team.getCourse(), CourseDTO.class))
                .orElseThrow(() -> new TeamNotFoundException("Team `" + id + "` does not exist"));
    }

    private String getPredefinedRegisterMessage(String id, String pwd) {
        return "Welcome to SpringExample app!\n\n" +
                "Your access credentials are:\n" +
                "-Username: " + id +
                "\n-Password: " + pwd + "" +
                "\n\nAuthenticate through http://localhost:8080/API/authenticate and enjoy!";
    }
}
