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

    @GetMapping("/{courseId}")
    public CourseDTO getOne(@PathVariable String courseId) {
        log.info("getOne(" + courseId + ") called");
        return courseService.getCourse(courseId)
                .map(ModelHelper::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course `" + courseId + "` does not exist"));
    }

    @GetMapping("/{courseId}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable String courseId) {
        log.info("enrolledStudents(" + courseId + ") called");
        try {
            return courseService.getEnrolledStudents(courseId)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/professors")
    public List<ProfessorDTO> getProfessors(@PathVariable String courseId) {
        log.info("getProfessor(" + courseId + ") called");
        try {
            return courseService.getCourseProfessors(courseId)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/teams")
    public List<TeamDTO> getTeams(@PathVariable String courseId) {
        log.info("getTeams(" + courseId + ") called");
        try {
            return teamService.getTeamsForCourse(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/availableStudents")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseId) {
        log.info("getAvailableStudents(" + courseId + ") called");
        try {
            return teamService.getAvailableStudents(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/unavailableStudents")
    public List<StudentDTO> getUnavailableStudents(@PathVariable String courseId) {
        log.info("getUnavailableStudents(" + courseId + ") called");
        try {
            return teamService.getStudentsInTeams(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/enabled")
    public boolean getEnabled(@PathVariable String courseId) {
        log.info("getEnabled(" + courseId + ") called");
        return courseService.getCourse(courseId)
                .map(CourseDTO::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, courseId));
    }

    @PostMapping({"", "/"})
    public CourseDTO add(@RequestBody CourseDTO courseDTO) {
        log.info("add(" + courseDTO + ") called");
        if (!courseService.addCourse(courseDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course `" + courseDTO.getAcronym() + "` already exists");
        return ModelHelper.enrich(courseDTO);
    }

    @PutMapping("/{courseId}/enabled")
    public void enableDisable(@PathVariable String courseId, @RequestBody Map<String, Boolean> reqBody) {
        log.info("enableDisable(" + courseId + ", " + reqBody + ") called");
        Boolean enable = reqBody.get("enabled");
        if (enable == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enabled {true,false} required");
        try {
            if (enable) courseService.enableCourse(courseId);
            else courseService.disableCourse(courseId);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (CourseProfessorNotAssigned e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/professor")
    public boolean setProfessor(@PathVariable String courseId, @RequestBody Map<String, String> reqBody) {
        log.info("setProfessor(" + courseId + ", " + reqBody + ") called");
        String professor = reqBody.get("id");
        if (professor == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return courseService.addProfessorToCourse(professor, courseId);
        } catch (CourseNotFoundException | ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("{courseId}/enrollOne")
    public boolean enrollStudent(@RequestBody Map<String, String> reqBody, @PathVariable String courseId) {
        log.info("enrollStudent(" + courseId + ", " + reqBody + ") called");
        String studentId = reqBody.get("id");
        if (studentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return studentService.addStudentToCourse(studentId, courseId);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("{courseId}/enrollMany")
    public List<Boolean> enrollStudents(@PathVariable String courseId, @RequestParam("file") MultipartFile multipartFile) {
        log.info("enrollStudents(" + courseId + ", " + multipartFile + ") called");
        if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        try {
            return studentService.addAndEnroll(new InputStreamReader(multipartFile.getInputStream()), courseId);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/teams")
    public TeamDTO proposeTeam(@PathVariable String courseId, @RequestBody Map<String, Object> reqBody) {
        log.info("proposeTeam(" + courseId + ", " + reqBody + ") called");
        String teamName = (String) reqBody.get("name");
        List<String> memberIds = (List<String>) reqBody.get("memberIds");
        if (teamName == null || memberIds == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teamName and memberIds are required");

        try {
            return teamService.proposeTeam(courseId, teamName, memberIds);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
