package it.polito.ai.lab2;

import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.repositories.ProposalRepository;
import it.polito.ai.lab2.repositories.RoleRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

      // Insert roles in the DB
      if (!roleRepository.existsByName(Utility.ADMIN_ROLE)) {
        log.info("Creating Role " + Utility.ADMIN_ROLE);
        Role role = new Role();
        role.setName(Utility.ADMIN_ROLE);
        roleRepository.save(role);
      }
      if (!roleRepository.existsByName(Utility.PROFESSOR_ROLE)) {
        log.info("Creating Role " + Utility.PROFESSOR_ROLE);
        Role role = new Role();
        role.setName(Utility.PROFESSOR_ROLE);
        roleRepository.save(role);
      }
      if (!roleRepository.existsByName(Utility.STUDENT_ROLE)) {
        log.info("Creating Role " + Utility.STUDENT_ROLE);
        Role role = new Role();
        role.setName(Utility.STUDENT_ROLE);
        roleRepository.save(role);
      }

      // TODO: to test!
      // Team proposals management
      List<Proposal> proposals = proposalRepository.findAllByStatus(ProposalStatus.PENDING);
      Set<Long> deleted = new HashSet<>();
      Set<Long> scheduled = new HashSet<>();
      proposals.forEach(proposal -> {
        if (proposal.getDeadline().isBefore(LocalDate.now())) {
          // Create scheduled task
          if (!scheduled.contains(proposal.getTeamId())) {
            scheduled.add(proposal.getTeamId());
            Runnable proposalDeadline = () -> {
              log.info("Deadline for proposal ");
              List<Proposal> props = proposalRepository.findAllByTeamId(proposal.getTeamId());
              boolean toDelete = props.stream()
                  .anyMatch(p -> p.getStatus().equals(ProposalStatus.REJECTED)
                      || p.getStatus().equals(ProposalStatus.PENDING));
              if (toDelete) {
                props.forEach(prop -> {
                  prop.setStatus(ProposalStatus.REJECTED);
                  proposalRepository.save(prop);
                });
                teamRepository.deleteById(proposal.getTeamId());
              }
            };
            scheduler.schedule(proposalDeadline,
                new Date(proposal.getDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()));
          }
        } else {
          // Reject proposals and delete team
          if (!deleted.contains(proposal.getTeamId())) {
            deleted.add(proposal.getTeamId());
            teamRepository.deleteById(proposal.getTeamId());
          }
          proposal.setStatus(ProposalStatus.REJECTED);
          proposalRepository.save(proposal);
        }
      });
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(SprangularBackend.class, args);
  }
}
