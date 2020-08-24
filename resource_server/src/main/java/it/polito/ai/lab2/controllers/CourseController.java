package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.exceptions.CourseNotFoundException;
import it.polito.ai.lab2.exceptions.CourseProfessorNotAssigned;
import it.polito.ai.lab2.exceptions.ProfessorNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.services.CourseService;
import it.polito.ai.lab2.services.StudentService;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.utility.ModelHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log(topic = "CourseController")
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService teamService;

    @Autowired
    CourseService courseService;

    @Autowired
    StudentService studentService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        log.info("all() called");
        return courseService.getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name) {
        log.info("getOne(" + name + ") called");
        return courseService.getCourse(name)
                .map(ModelHelper::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course `" + name + "` does not exist"));
    }

    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable String name) {
        log.info("enrolledStudents(" + name + ") called");
        try {
            return courseService.getEnrolledStudents(name)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{name}/professors")
    public List<ProfessorDTO> getProfessor(@PathVariable String name) {
        log.info("getProfessor(" + name + ") called");
        try {
            return courseService.getCourseProfessors(name)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{name}/teams")
    public List<TeamDTO> getTeams(@PathVariable String name) {
        log.info("getTeams(" + name + ") called");
        try {
            return teamService.getTeamsForCourse(name).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{name}/availableStudents")
    public List<StudentDTO> getAvailableStudents(@PathVariable String name) {
        log.info("getAvailableStudents(" + name + ") called");
        try {
            return teamService.getAvailableStudents(name).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{name}/unavailableStudents")
    public List<StudentDTO> getUnavailableStudents(@PathVariable String name) {
        log.info("getUnavailableStudents(" + name + ") called");
        try {
            return teamService.getStudentsInTeams(name).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{name}/enabled")
    public boolean getEnabled(@PathVariable String name) {
        log.info("getEnabled(" + name + ") called");
        return courseService.getCourse(name)
                .map(CourseDTO::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, name));
    }

    @PostMapping({"", "/"})
    public CourseDTO add(@RequestBody CourseDTO courseDTO) {
        log.info("add(" + courseDTO + ") called");
        if (!courseService.addCourse(courseDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course `" + courseDTO.getName() + "` already exists");
        return ModelHelper.enrich(courseDTO);
    }

    @PutMapping("/{name}/enabled")
    public void enableDisable(@PathVariable String name, @RequestBody Map<String, Boolean> reqBody) {
        log.info("enableDisable(" + name + ", " + reqBody + ") called");
        Boolean enable = reqBody.get("enabled");
        if (enable == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enabled {true,false} required");
        try {
            if (enable) courseService.enableCourse(name);
            else courseService.disableCourse(name);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (CourseProfessorNotAssigned e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{name}/professor")
    public boolean setProfessor(@PathVariable String name, @RequestBody Map<String, String> reqBody) {
        log.info("setProfessor(" + name + ", " + reqBody + ") called");
        String professor = reqBody.get("id");
        if (professor == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return courseService.addProfessorToCourse(professor, name);
        } catch (CourseNotFoundException | ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("{name}/enrollOne")
    public boolean enrollStudent(@RequestBody Map<String, String> reqBody, @PathVariable String name) {
        log.info("enrollStudent(" + name + ", " + reqBody + ") called");
        String studentId = reqBody.get("id");
        if (studentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return studentService.addStudentToCourse(studentId, name);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping(value = "{name}/enrollMany")
    public List<Boolean> enrollStudents(@PathVariable String name, @RequestParam("file") MultipartFile multipartFile) {
        log.info("enrollStudents(" + name + ", " + multipartFile + ") called");
        if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        try {
            return studentService.addAndEnroll(new InputStreamReader(multipartFile.getInputStream()), name);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{name}/teams")
    public TeamDTO proposeTeam(@PathVariable String name, @RequestBody Map<String, Object> reqBody) {
        log.info("proposeTeam(" + name + ", " + reqBody + ") called");
        String teamName = (String) reqBody.get("name");
        List<String> memberIds = (List<String>) reqBody.get("memberIds");
        if (teamName == null || memberIds == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teamName and memberIds are required");

        try {
            return teamService.proposeTeam(name, teamName, memberIds);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
