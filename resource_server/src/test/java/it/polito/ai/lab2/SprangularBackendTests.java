package it.polito.ai.lab2;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.services.CourseService;
import it.polito.ai.lab2.services.StudentService;
import it.polito.ai.lab2.services.TeamService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/*If @Transaction => doesn't save to DB so tests fail! Don't know how to solve, so the DB must not have those entry, and it won't be clean */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SprangularBackendTests {

  static final String studentID = "s100";
  static final String courseID = "ApplicazioniInternet";

  @Resource
  TeamService teamService;

  @Resource
  StudentService studentService;

  @Resource
  CourseService courseService;

  @Test
  @Order(1)
  void contextLoads() {
    assertThat(studentID).isNotEmpty();
    assertThat(courseID).isNotEmpty();
    assertThat(teamService).isNotNull();
  }

  @Test
  @Order(4)
  void testDisableCourse() {
    courseService.disableCourse(courseID);
    CourseDTO cc = courseService.getCourse(courseID).orElse(null);
    assertThat(cc).isNotNull();
    assertThat(cc.isEnabled()).isFalse();
  }

  @Test
  @Order(5)
  void testEnableCourse() {
    courseService.enableCourse(courseID);
    CourseDTO c = courseService.getCourse(courseID).orElse(null);
    assertThat(c).isNotNull();
    assertThat(c.isEnabled()).isTrue();
  }

  @Test
  @Order(6)
  void testAddStudentToCourse() {
    Boolean verifier = courseService.getEnrolledStudents(courseID).stream().noneMatch(x -> x.getId().equals(studentID));
    assertThat(courseService.addStudentToCourse(studentID, courseID)).isEqualTo(verifier);
  }

  @Test
  @Order(7)
  void testGetAllCourses() {
    assertThat(courseService.getAllCourses().size()).isGreaterThanOrEqualTo(1);
  }

  @Test
  @Order(8)
  void testGetAllStudents() {
    assertThat(studentService.getAllStudents().size()).isGreaterThanOrEqualTo(1);
  }

}
