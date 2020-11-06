package it.polito.ai.lab2;

import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.AssignmentStatus;
import it.polito.ai.lab2.utility.ProposalStatus;
import it.polito.ai.lab2.utility.Utility;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@Log
@SpringBootApplication
public class SprangularBackend {

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  TaskScheduler scheduler;

  @Autowired
  AssignmentSolutionRepository assignmentSolutionRepository;

  @Autowired
  UploadRepository uploadRepository;

  @Bean
  ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  CommandLineRunner getCommandLineRunner() {
    return args -> {
      // Create directory for file storage
      try {
        Files.createDirectory(Paths.get(Utility.IMAGES_ROOT_DIR));
        Files.createDirectory(Utility.PHOTOS_DIR);
        Files.createDirectory(Utility.ASSIGNMENTS_DIR);
        Files.createDirectory(Utility.UPLOADS_DIR);
        Files.createDirectory(Utility.VM_MODELS_DIR);
        Files.createDirectory(Utility.VMS_DIR);
        log.info("Uploads directory created");
      } catch (FileAlreadyExistsException e) {
        log.info("Uploads directory already exists");
      }

      // Team pending active proposals management
      Set<Long> scheduled = new HashSet<>();
      proposalRepository.findAllByStatus(ProposalStatus.PENDING).stream()
          .filter(Proposal::isValid)
          .forEach(proposal -> {
        if (proposal.getDeadline().after(new Timestamp(System.currentTimeMillis()))) {
          // Create scheduled task for the team corresponding to the proposal
          Long teamId = proposal.getTeamId();
          if (!scheduled.contains(teamId)) {
            scheduled.add(teamId);
            Runnable proposalDeadline = () -> {
              log.info("Deadline for proposal ");
              List<Proposal> props = proposalRepository.findAllByTeamId(teamId);
              props.forEach(p -> p.setValid(false));
              props.stream()
                  .filter(p -> p.getStatus().equals(ProposalStatus.PENDING))
                  .forEach(p -> {
                    p.setStatus(ProposalStatus.REJECTED);
                    proposalRepository.save(p);
                  });
            };
            scheduler.schedule(proposalDeadline, new Date(proposal.getDeadline().getTime()));
          }
        } else {
          proposal.setValid(false);
          proposal.setStatus(ProposalStatus.REJECTED);
          proposalRepository.save(proposal);
        }
      });

      // Assignment delivery management
      Set<Long> programmed = new HashSet<>();
      assignmentSolutionRepository.findAllByStatusIn(
          Arrays.asList(AssignmentStatus.NULL, AssignmentStatus.READ, AssignmentStatus.REVIEWED_UPLOADABLE))
          .forEach(assignmentSolution -> {
        if (assignmentSolution.getAssignment().getDueDate().before(new Timestamp(System.currentTimeMillis()))) {
          // Deliver assignment
          Timestamp currentTs = new Timestamp(System.currentTimeMillis());
          assignmentSolution.setStatus(AssignmentStatus.DELIVERED);
          assignmentSolution.setStatusTs(currentTs);
          Upload upload = new Upload();
          upload.setAuthor(assignmentSolution.getStudent().getId());
          upload.setTimestamp(currentTs);
          upload.setStatus(AssignmentStatus.DELIVERED);
          upload.setComment("Assignment automatically delivered");
          upload.setAssignmentSolution(assignmentSolution);
          uploadRepository.save(upload);
          assignmentSolutionRepository.save(assignmentSolution);
        } else {
          // Schedule assignment delivery
          Long assId = assignmentSolution.getAssignment().getId();
          if (!programmed.contains(assId)) {
            programmed.add(assId);
            Assignment assignment = assignmentSolution.getAssignment();
            Runnable automaticDelivery = () -> assignment.getSolutions().stream()
                .filter(as -> as.getStatus().equals(AssignmentStatus.NULL)
                    || as.getStatus().equals(AssignmentStatus.READ)
                    || as.getStatus().equals(AssignmentStatus.REVIEWED_UPLOADABLE)).forEach(
                solution -> {
                  Timestamp currentTs = new Timestamp(System.currentTimeMillis());
                  solution.setStatus(AssignmentStatus.DELIVERED);
                  solution.setStatusTs(currentTs);
                  Upload upload = new Upload();
                  upload.setAuthor(assignmentSolution.getStudent().getId());
                  upload.setTimestamp(currentTs);
                  upload.setStatus(AssignmentStatus.DELIVERED);
                  upload.setComment("Assignment automatically delivered");
                  upload.setAssignmentSolution(assignmentSolution);
                  uploadRepository.save(upload);
                  assignmentSolutionRepository.save(solution);
                }
            );
            scheduler.schedule(automaticDelivery,
                new Date(assignment.getDueDate().getTime()));
          }
        }
      });
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(SprangularBackend.class, args);
  }
}
