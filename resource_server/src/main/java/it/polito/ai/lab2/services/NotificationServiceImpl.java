package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.repositories.ProposalRepository;
import it.polito.ai.lab2.utility.ProposalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@EnableScheduling
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  JavaMailSender emailSender;

  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  TeamService teamService;

  @Override
  @Async
  public void sendMessage(String address, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("s264970@studenti.polito.it"); //TODO: cambia!
    message.setSubject(subject);
    message.setText(body);
    //emailSender.send(message); //TODO: uncomment to send mails
  }

  @Override
  public boolean confirm(String token) {
    Proposal proposal = proposalRepository.findById(token).orElse(null);

    if (proposal == null) {
      return false;
    }

    if (proposal.getDeadline().isBefore(LocalDate.now())) { //if the proposal is expired I delete all the proposals
      proposalRepository.deleteAll(proposalRepository.findAllByTeamId(proposal.getTeamId()));
      return false;
    }

    proposal.setStatus(ProposalStatus.ACCEPTED);
    proposalRepository.save(proposal);

    for (Proposal p : proposalRepository.findAllByTeamId(proposal.getTeamId())) {
      if (p.getStatus() == ProposalStatus.PENDING) {
        return false; //There is at least one proposal which is in pending status
      }
    }

    teamService.activateTeam(proposal.getTeamId());
    proposalRepository.deleteAll(proposalRepository.findAllByTeamId(proposal.getTeamId()));
    return true;
  }

  @Override
  public boolean reject(String token) {
    Proposal proposal = proposalRepository.findById(token).orElse(null);

    if (proposal == null) {
      return false;
    }

    if (proposal.getDeadline().isBefore(LocalDate.now())) { //if the proposal is expired I delete all the proposals
      proposalRepository.deleteAll(proposalRepository.findAllByTeamId(proposal.getTeamId()));
      return false;
    }

    proposalRepository.deleteAll(proposalRepository.findAllByTeamId(proposal.getTeamId()));
    teamService.evictTeam(proposal.getTeamId());
    return true;
  }

  @Override
  public void notifyTeam(TeamDTO dto, List<String> memberIds, String courseId) {
    String members = String.join("\n- ", memberIds);
    String url = "http://localhost:8080/API/courses/" + courseId; //TODO: fatti dare link giusto

    memberIds.remove(SecurityContextHolder.getContext().getAuthentication().getName());
    memberIds.forEach(memberId -> {
      String email = memberId + "@studenti.polito.it";
      sendMessage(email, "[SpringExample] Someone wants to create a team with you",
          "Dear " + email + ",\n\n" +
              "You have been requested to create the team: " + dto.getName() + "\n\n" +
              "The members would be:\n- " + members +
              "\n\nTo confirm or reject the proposal enter in your personal page at: " + url +
              "\n\nBest Regards,\nSpringExample Team");
    });
  }

  @Scheduled(fixedDelay = 60 * 60 * 1000)
  public void fixedTokenClear() {
    Set<Long> teamIds = new HashSet<>();
    proposalRepository.findAllByDeadlineAfter(LocalDate.now()).forEach(proposal -> {
      teamIds.add(proposal.getTeamId());
      proposalRepository.delete(proposal);
    });
    teamIds.forEach(team -> teamService.evictTeam(team));
  }
}
