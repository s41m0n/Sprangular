package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.ProposalStatus;
import it.polito.ai.lab2.utility.TeamProposalDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR') or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId))")
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getTeams().stream()
                        .map(team -> modelMapper.map(team, TeamDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#teamId)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#teamId))")
    public List<StudentDTO> getTeamMembers(Long teamId) {
        return teamRepository.findById(teamId)
                .map(team -> team.getMembers().stream()
                        .map(student -> modelMapper.map(student, StudentDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseId) and @securityServiceImpl.isStudentInTeamRequest(#memberIds)")
    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds, int hoursTimeout) {
        if (memberIds.stream().distinct().count() != memberIds.size())
            throw new DuplicateStudentInTeam("Some student is already in the group " + name);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));

        if (!course.isEnabled()) throw new CourseNotEnabledException("Course " + courseId + " is not enabled");


        if (course.getTeams().stream().anyMatch(x -> x.getName().equals(name)))
            throw new TeamNameAlreadyInCourseException("Team `" + name + "` already in course `" + courseId + "`");

        if (memberIds.size() > course.getTeamMaxSize() || memberIds.size() < course.getTeamMinSize())
            throw new IllegalTeamMemberException("For the course " + courseId + " team must be composed between " + course.getTeamMinSize() + " and " + course.getTeamMaxSize() + " students");

        List<Student> members = studentRepository.findAllById(memberIds);

        if (members.size() != memberIds.size())
            throw new StudentNotFoundException("Some student does not exist in the database");

        if (!course.getStudents().containsAll(members))
            throw new StudentNotInCourseException("Some student is not enrolled in course " + courseId);

        if (members.stream()
                .anyMatch(student -> student.getTeams().stream()
                        .anyMatch(team -> team.getCourse().getName().equals(courseId))))
            throw new StudentAlreadyInTeam("Some student is already in a team for the course " + courseId);

        boolean isAlone = memberIds.size() == 1;
        Team team = new Team();
        team.setMembers(members);
        team.setCourse(course);
        team.setName(name);
        team.setStatus(isAlone ? 1 : 0);
        TeamDTO t = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        if (!isAlone) notificationService.notifyTeam(t, memberIds);
        return t;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<TeamDTO> getTeamsForCourse(String courseName) {
        return courseRepository.findById(courseName)
                .map(course -> course.getTeams().stream()
                        .map(team -> modelMapper.map(team, TeamDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        return courseRepository.getStudentsInTeams(courseName).stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getAvailableStudents(String courseName) {
        return courseRepository.getStudentsNotInTeams(courseName).stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void activeTeam(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException("Team + " + id + " does not exist"));
        team.setStatus(1);
    }

    @Override
    public void evictTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TeamDTO> getTeams() {
        return teamRepository.findAll().stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#id)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#id))")
    public Optional<TeamDTO> getTeam(Long id) {
        return teamRepository.findById(id)
                .map(t -> modelMapper.map(t, TeamDTO.class));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#id)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isTeamOfStudentCourse(#id))")
    public CourseDTO getCourseForTeam(Long id) {
        return teamRepository.findById(id)
                .map(team -> modelMapper.map(team.getCourse(), CourseDTO.class))
                .orElseThrow(() -> new TeamNotFoundException("Team `" + id + "` does not exist"));
    }

    @Override
    public List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseName) {
        studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
        Course course = courseRepository.findById(courseName).orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));

        List<Proposal> proposals = proposalRepository.findAllByInvitedUserIdAndCourseId(studentId, course.getId());

        if(proposals.isEmpty()){
            return null;
        }

        List<TeamProposalDetails> proposalsDetails = new ArrayList<>();

        for(Proposal p : proposals) {
            TeamProposalDetails tpd = new TeamProposalDetails();
            tpd.setTeamName(teamRepository.getOne(p.getTeamId()).getName());
            tpd.setProposalCreator(studentRepository.getOne(p.getProposalCreatorId()));

            Map<Student, ProposalStatus> teamApprovalDetails = new HashMap<>();

            for(Proposal pr : proposalRepository.findAllByTeamId(p.getTeamId())){
                teamApprovalDetails.put(studentRepository.getOne(pr.getInvitedUserId()), pr.getStatus());
            }

            tpd.setMembersAndStatus(teamApprovalDetails);

            proposalsDetails.add(tpd);
        }

        return proposalsDetails;
    }

    @Override
    public boolean setVmsResourceLimits(Long teamId, int vCpu, int diskStorage, int ram, int maxActiveInstances, int maxTotalInstances) { //only professor
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));

        int actualVCpu = 0;
        int actualRam = 0;
        int actualDiskStorage = 0;
        int numOfActiveVms = 0;

        for(Vm vm : team.getVms()){
            actualVCpu += vm.getVCpu();
            actualRam += vm.getRam();
            actualDiskStorage += vm.getDiskStorage();
            if(vm.isActive()){
                numOfActiveVms += 1;
            }
        }

        if(actualVCpu > vCpu || actualRam > ram || actualDiskStorage > diskStorage || numOfActiveVms > maxActiveInstances || team.getVms().size() > maxTotalInstances){
            throw new TooManyActualResourcesException("Cannot set VMs resource limits, actual used resources are higher than the new limits");
        }

        team.setMaxVCpu(vCpu);
        team.setMaxRam(ram);
        team.setMaxDiskStorage(diskStorage);
        team.setMaxActiveInstances(maxActiveInstances);
        team.setMaxTotalInstances(maxTotalInstances);
        teamRepository.save(team);
        return true;
    }
}
