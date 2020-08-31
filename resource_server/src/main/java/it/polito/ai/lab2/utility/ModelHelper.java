package it.polito.ai.lab2.utility;

import it.polito.ai.lab2.controllers.CourseController;
import it.polito.ai.lab2.controllers.ProfessorController;
import it.polito.ai.lab2.controllers.StudentController;
import it.polito.ai.lab2.controllers.TeamController;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ModelHelper {

    static public CourseDTO enrich(CourseDTO courseDTO) {
        courseDTO.add(Arrays.asList(
                linkTo(methodOn(CourseController.class).enrolledStudents(courseDTO.getName())).withRel("enrolled"),
                linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel(),
                linkTo(methodOn(CourseController.class).getProfessors(courseDTO.getName())).withRel("professors"),
                linkTo(methodOn(CourseController.class).getTeams(courseDTO.getName())).withRel("teams"),
                linkTo(methodOn(CourseController.class).getAvailableStudents(courseDTO.getName())).withRel("availableStudents"),
                linkTo(methodOn(CourseController.class).getUnavailableStudents(courseDTO.getName())).withRel("unavailableStudents")
        ));
        return courseDTO;
    }

    static public StudentDTO enrich(StudentDTO studentDTO) {
        studentDTO.add(Arrays.asList(
                linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel(),
                linkTo(methodOn(StudentController.class).getTeams(studentDTO.getId())).withRel("teams"),
                linkTo(methodOn(StudentController.class).getCourses(studentDTO.getId())).withRel("courses")
        ));
        return studentDTO;
    }

    static public ProfessorDTO enrich(ProfessorDTO professorDTO) {
        professorDTO.add(Arrays.asList(
                linkTo(ProfessorController.class).slash(professorDTO.getId()).withSelfRel(),
                linkTo(methodOn(ProfessorController.class).getProfessorCourses(professorDTO.getId())).withRel("courses")
                ));
        return professorDTO;
    }

    static public TeamDTO enrich(TeamDTO teamDTO) {
        teamDTO.add(Arrays.asList(
                linkTo(TeamController.class).slash(teamDTO.getId()).withSelfRel(),
                linkTo(methodOn(TeamController.class).getMembers(teamDTO.getId())).withRel("members"),
                linkTo(methodOn(TeamController.class).getCourse(teamDTO.getId())).withRel("course")
        ));
        return teamDTO;
    }
}
