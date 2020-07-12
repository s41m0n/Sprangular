package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.exceptions.ProfessorNotFoundException;
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
@Log(topic = "ProfessorController")
@RequestMapping("/API/professors")
public class ProfessorController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    public List<ProfessorDTO> all() {
        log.info("all() called");
        return teamService.getProfessors().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProfessorDTO getOne(@PathVariable String id) {
        log.info("getOne(" + id + ") called");
        return teamService.getProfessor(id)
                .map(ModelHelper::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor `" + id + "` does not exist"));
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getProfessorCourses(@PathVariable String id) {
        log.info("getProfessorCourses(" + id + ") called");
        try {
            return teamService.getProfessorCourses(id).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public ProfessorDTO add(@RequestBody ProfessorDTO professorDTO) {
        log.info("add(" + professorDTO + ") called");
        if(!teamService.addProfessor(professorDTO)) throw new ResponseStatusException(HttpStatus.CONFLICT, professorDTO.getId());
        return ModelHelper.enrich(professorDTO);
    }

}
