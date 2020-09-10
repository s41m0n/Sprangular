package it.polito.ai.lab2.controllers;

import com.sun.istack.NotNull;
import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.TeamProposalRequest;
import it.polito.ai.lab2.pojos.UpdateCourseDetails;
import it.polito.ai.lab2.services.CourseService;
import it.polito.ai.lab2.services.StudentService;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.services.VmService;
import it.polito.ai.lab2.utility.ModelHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Autowired
    VmService vmService;

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
        try {
            if (!courseService.addCourse(courseDTO))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + courseDTO.getAcronym() + " already exists");
            return ModelHelper.enrich(courseDTO);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with the same name (" + courseDTO.getName() + ") already exists");
        }
    }

    @PutMapping("/{courseId}/enabled")
    public void enableDisable(@PathVariable String courseId, @NotNull @RequestBody Map<String, Boolean> reqBody) {
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

    @PutMapping("/{courseId}/addProfessor")
    public boolean addProfessor(@PathVariable String courseId, @NotNull @RequestBody Map<String, String> reqBody) {
        log.info("setProfessor(" + courseId + ", " + reqBody + ") called");
        String professor = reqBody.get("professorId");
        if (professor == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return courseService.addProfessorToCourse(professor, courseId);
        } catch (CourseNotFoundException | ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UserNotVerifiedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("{courseId}/enrollOne")
    public boolean enrollStudent(@NotNull @RequestBody Map<String, String> reqBody, @PathVariable String courseId) {
        log.info("enrollStudent(" + courseId + ", " + reqBody + ") called");
        String studentId = reqBody.get("studentId");
        if (studentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id required");
        try {
            return courseService.addStudentToCourse(studentId, courseId);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UserNotVerifiedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("{courseId}/enrollMany")
    public List<Boolean> enrollStudents(@PathVariable String courseId, @RequestParam("file") MultipartFile multipartFile) {
        log.info("enrollStudents(" + courseId + ", " + multipartFile + ") called");
        if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        try {
            return courseService.enrollAll(new InputStreamReader(multipartFile.getInputStream()), courseId);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/teams")
    public TeamDTO proposeTeam(@PathVariable String courseId, @RequestBody TeamProposalRequest proposal) {
        log.info("proposeTeam(" + courseId + ", " + proposal.getTeamName() + ") called");
        if (proposal.getTeamName() == null
                || proposal.getDeadline() == null
                || proposal.getStudentIds() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teamName, memberIds and deadline are required");

        try {
            return teamService.proposeTeam(courseId, proposal.getTeamName(), proposal.getStudentIds(), proposal.getDeadline());
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/vmModel")
    public VmModelDTO createVmModel(@PathVariable String courseId, @RequestBody VmModelDTO vmModelDTO) {
        try {
            if (vmService.createVmModel(vmModelDTO, courseId)) {
                return vmModelDTO;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + courseId + " already has a VM model");
            }
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/vmModel")
    public VmModelDTO updateVmModel(@PathVariable String courseId, @RequestBody VmModelDTO vmModelDTO) {
        try {
            return vmService.updateVmModel(vmModelDTO, courseId);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/vms")
    public List<VmDTO> getVmsOfCourse(@PathVariable String courseId) {
        try {
            return vmService.getVmsOfCourse(courseId);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/removeStudent")
    public StudentDTO removeStudentFromCourse(@PathVariable String courseId, @NotNull @RequestBody Map<String, String> reqBody) {
        try {
            String studentId = reqBody.get("studentId");
            return courseService.removeStudentFromCourse(studentId, courseId);
        } catch (StudentNotFoundException | CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (StudentNotInCourseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/removeProfessor")
    public ProfessorDTO removeProfessorFromCourse(@PathVariable String courseId, @NotNull @RequestBody Map<String, String> reqBody) {
        try {
            String professorId = reqBody.get("professorId");
            return courseService.removeProfessorFromCourse(professorId, courseId);
        } catch (ProfessorNotFoundException | CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}")
    public CourseDTO deleteCourse(@PathVariable String courseId) {
        try {
            return courseService.removeCourse(courseId);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (CourseNotEmptyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{courseId}")
    public CourseDTO updateCourse(@PathVariable String courseId, @RequestBody UpdateCourseDetails updateCourseDetails) {
        try {
            CourseDTO c = courseService.updateCourse(courseId, updateCourseDetails);
            if (c == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TeamMaxSize has to be greater than TeamMinSize");
            }
            return c;
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
