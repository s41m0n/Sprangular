package it.polito.ai.lab2;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.services.TeamService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/*If @Transaction => doesn't save to DB so tests fail! Don't know how to solve, so the DB must not have those entry, and it won't be clean */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SprangularBackendTests {

    static final String studentID = "s100";
    static final String courseID = "ApplicazioniInternet";

    @Resource
    TeamService teamService;

    @Test
    @Order(1)
    void contextLoads() {
        assertThat(studentID).isNotEmpty();
        assertThat(courseID).isNotEmpty();
        assertThat(teamService).isNotNull();
    }

    @Test
    @Order(2)
    void testAddStudent() {
        Boolean verifier = teamService.getStudent(studentID).orElse(null) == null;
        StudentDTO s = new StudentDTO();
        s.setId(studentID);
        s.setName("Test");
        s.setFirstName("Test");
        assertThat(teamService.addStudent(s)).isEqualTo(verifier);
    }

    @Test
    @Order(3)
    void testAddCourse() {
        Boolean verifier = teamService.getCourse(courseID).orElse(null) == null;
        CourseDTO c = new CourseDTO();
        c.setEnabled(true);
        c.setMax(2);
        c.setMin(1);
        c.setName(courseID);
        assertThat(teamService.addCourse(c)).isEqualTo(verifier);
    }

    @Test
    @Order(4)
    void testDisableCourse() {
        teamService.disableCourse(courseID);
        CourseDTO cc = teamService.getCourse(courseID).orElse(null);
        assertThat(cc).isNotNull();
        assertThat(cc.isEnabled()).isFalse();
    }

    @Test
    @Order(5)
    void testEnableCourse() {
        teamService.enableCourse(courseID);
        CourseDTO c = teamService.getCourse(courseID).orElse(null);
        assertThat(c).isNotNull();
        assertThat(c.isEnabled()).isTrue();
    }

    @Test
    @Order(6)
    void testAddStudentToCourse() {
        Boolean verifier = teamService.getEnrolledStudents(courseID).stream().noneMatch(x -> x.getId().equals(studentID));
        assertThat(teamService.addStudentToCourse(studentID, courseID)).isEqualTo(verifier);
    }

    @Test
    @Order(7)
    void testGetAllCourses() {
        assertThat(teamService.getAllCourses().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(8)
    void testGetAllStudents() {
        assertThat(teamService.getAllStudents().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(9)
    void testAddAndEnroll() {
        try {
            Reader r = new BufferedReader(new FileReader("/home/s41m0n/Desktop/ApplicazioniInternet/labs/students.csv"));
            /*Checking only that it does not throw exception*/
            teamService.addAndEnroll(r, courseID);
        }catch (IOException e) {
            fail("File not found");
        }
    }

}
