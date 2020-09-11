package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.SetVmsResourceLimits;
import it.polito.ai.lab2.pojos.UpdateVmDetails;
import it.polito.ai.lab2.services.NotificationService;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.services.VmService;
import it.polito.ai.lab2.utility.ModelHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log(topic = "TeamController")
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    VmService vmService;

    @Autowired
    NotificationService notificationService;

    @GetMapping({"", "/"})
    public List<TeamDTO> all() {
        log.info("all() called");
        return teamService.getTeams().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TeamDTO getOne(@PathVariable Long id) {
        log.info("getOne(" + id + ") called");
        return teamService.getTeam(id)
                .map(ModelHelper::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team `" + id + "` does not exist"));
    }

    @GetMapping("/{id}/members")
    public List<StudentDTO> getMembers(@PathVariable Long id) {
        log.info("getMembers(" + id + ") called");
        try {
            return teamService.getTeamMembers(id).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/course")
    public CourseDTO getCourse(@PathVariable Long id) {
        log.info("getCourse(" + id + ") called");
        try {
            return ModelHelper.enrich(teamService.getCourseForTeam(id));
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/vms/{vmId}")
    public VmDTO getVm(@PathVariable Long teamId, @PathVariable Long vmId) {
        try {
            return vmService.getVm(vmId, teamId);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{id}/vms")
    public VmDTO createVm(@PathVariable Long id, @RequestBody VmDTO vmDTO) {
        try {
            return vmService.createVm(id, vmDTO);
        } catch (VmModelNotFoundException | StudentNotFoundException | CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/vms/{vmId}")
    public VmDTO deleteVm(@PathVariable Long teamId, @PathVariable Long vmId) {
        try {
            return vmService.deleteVm(vmId, teamId);
        } catch (CannotDeleteVmException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{teamId}/vms/{vmId}")
    public VmDTO updateVm(@PathVariable Long teamId, @PathVariable Long vmId, @RequestBody UpdateVmDetails uvd) {
        try {
            return vmService.updateVmResources(vmId, teamId, uvd);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{teamId}/vms/{vmId}/turnOnVm")
    public VmDTO turnOnVm(@PathVariable Long teamId, @PathVariable Long vmId) {
        try {
            return vmService.turnOnVm(vmId, teamId);
        } catch (VmNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (MaxVmResourcesException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{teamId}/vms/{vmId}/turnOffVm")
    public VmDTO turnOffVm(@PathVariable Long teamId, @PathVariable Long vmId) {
        try {
            return vmService.turnOffVm(vmId, teamId);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{teamId}/vms/{vmId}/addOwner")
    public void addVmOwner(@PathVariable Long teamId, @PathVariable Long vmId, @RequestBody Map<String, String> reqBody) {
        try {
            vmService.addVmOwner(vmId, teamId, reqBody.get("studentId"));
        } catch (VmNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VmNotOfTeamException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (UserNotVerifiedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/vms")
    public List<VmDTO> getVmsOfTeam(@PathVariable Long teamId){
        try {
            return vmService.getVmsOfTeam(teamId);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{teamId}/updateVmsResourceLimits")
    public TeamDTO updateVmsResourceLimits(@PathVariable Long teamId, SetVmsResourceLimits setVmsResourceLimits){
        try {
            return teamService.setVmsResourceLimits(teamId, setVmsResourceLimits);
        } catch (TooManyActualResourcesException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/confirmInvitation/{token}")
    public boolean confirmInvitation(@PathVariable String token) {
        if (!notificationService.confirm(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        }
        return true;
    }

    @GetMapping("/rejectInvitation/{token}")
    public boolean rejectInvitation(@PathVariable String token) {
        if (!notificationService.reject(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        }
        return true;
    }

}
