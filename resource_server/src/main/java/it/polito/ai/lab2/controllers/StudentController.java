package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.services.CustomUserDetailsService;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.utility.ModelHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log(topic = "StudentController")
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @GetMapping({"", "/"})
    public List<StudentDTO> all() {
        log.info("all() called");
        return teamService.getAllStudents().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable String id) {
        log.info("getOne(" + id + ") called");
        return teamService.getStudent(id)
                .map(ModelHelper::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student `" + id + "` does not exist"));
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getCourses(@PathVariable String id) {
        log.info("getCourses(" + id + ") called");
        try {
            return teamService.getCourses(id).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        }catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/teams")
    public List<TeamDTO> getTeams(@PathVariable String id) {
        log.info("getTeams(" + id + ") called");
        try {
            return teamService.getTeamsForStudent(id).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (StudentNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public StudentDTO add(@RequestBody StudentDTO studentDTO) {
        log.info("add(" + studentDTO + ") called");
        if(!teamService.addStudent(studentDTO)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already present student `" + studentDTO.getId() + "`");
        return ModelHelper.enrich(studentDTO);
    }
}
