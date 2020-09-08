package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.UpdateCourseDetails;
import it.polito.ai.lab2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
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
    StudentService studentService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public boolean addCourse(CourseDTO course) {
        if (course.getTeamMaxSize() < course.getTeamMinSize() || courseRepository.existsById(course.getAcronym())) {
            return false;
        }
        Course c = modelMapper.map(course, Course.class);
        courseRepository.save(c);
        if(professorRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).isPresent()){
            this.addProfessorToCourse(professorRepository.getOne(SecurityContextHolder.getContext().getAuthentication().getName()).getId(), course.getAcronym());
        }
        return true;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public Optional<CourseDTO> getCourse(String courseId) {
        return courseRepository.findById(courseId)
                .map(course -> modelMapper.map(course, CourseDTO.class));
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId))")
    public List<StudentDTO> getEnrolledStudents(String courseId) {
        return courseRepository.findById(courseId)
                .map(course -> course.getStudents().stream()
                        .map(student -> modelMapper.map(student, StudentDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public void enableCourse(String courseId) {
        courseRepository.findById(courseId)
                .ifPresentOrElse(course -> {
                    if (course.getProfessors().isEmpty())
                        throw new CourseProfessorNotAssigned("You can enable course " + courseId + " only when at least one professor has been assigned to it");
                    course.setEnabled(true);
                }, () -> {
                    throw new CourseNotFoundException("Course " + courseId + " does not exist");
                });
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public void disableCourse(String courseId) {
        courseRepository.findById(courseId)
                .ifPresentOrElse(course -> course.setEnabled(false), () -> {
                    throw new CourseNotFoundException("Course " + courseId + " does not exist");
                });
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public List<ProfessorDTO> getCourseProfessors(String courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
        return c.getProfessors() == null ? null : c.getProfessors().stream().map(p -> modelMapper.map(p, ProfessorDTO.class)).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public boolean addProfessorToCourse(String professor, String courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
        Professor p = professorRepository.findById(professor).orElseThrow(() -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
        p.addCourse(c);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public ProfessorDTO removeProfessorFromCourse(String professor, String courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
        Professor p = professorRepository.findById(professor).orElseThrow(() -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
        p.removeCourse(c);
        return modelMapper.map(p, ProfessorDTO.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public CourseDTO removeCourse(String courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course `" + courseId + "` does not exist"));
        if (c.getStudents().isEmpty()) {
            throw new CourseNotEmptyException("Course " + courseId + " has one or more students enrolled, you cannot delete it");
        }
        courseRepository.delete(c);
        return modelMapper.map(c, CourseDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public CourseDTO updateCourse(String courseId, UpdateCourseDetails updateCourseDetails) {
        if (updateCourseDetails.getTeamMaxSize() < updateCourseDetails.getTeamMinSize()) {
            return null;
        }
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
        c.setTeamMaxSize(updateCourseDetails.getTeamMaxSize());
        c.setTeamMinSize(updateCourseDetails.getTeamMinSize());
        c.setEnabled(updateCourseDetails.isEnabled());
        courseRepository.save(c);
        return modelMapper.map(c, CourseDTO.class);
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public List<Boolean> enrollAll(Reader r, String courseId) {
        courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();

        List<StudentDTO> enrolledStudents = this.getEnrolledStudents(courseId);

        if(enrolledStudents.containsAll(students)){
            return students.stream()
                    .map(studentId -> addStudentToCourse(studentId.getId(), courseId))
                    .collect(Collectors.toList());
        }
        throw new StudentNotInCourseException("One or more students are not enrolled in course " + courseId);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId))")
    public StudentDTO removeStudentFromCourse(String studentId, String courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        if (!course.getStudents().contains(student)) {
            throw new StudentNotInCourseException("Some student is not enrolled in course " + courseId);
        }

        student.removeCourse(course); //symmetric method, it updates also the course
        return modelMapper.map(student, StudentDTO.class);
    }
}
