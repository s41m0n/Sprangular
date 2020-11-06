package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.exceptions.TokenNotFoundException;
import it.polito.ai.lab2.repositories.ProposalRepository;
import it.polito.ai.lab2.utility.ProposalStatus;
import it.polito.ai.lab2.utility.Utility;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@EnableScheduling
@Log(topic = "NotificationServiceImpl")
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
    String sprangularAddress = "noreply.sprangular@gmail.com";
    log.warning("*** THE MAIL SHOULD BE SENT TO " + address +
        " BUT FOR OBVIOUS REASON WILL BE SENT TO OUR DESIGNED ADDRESS: " + sprangularAddress + " ***");
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(sprangularAddress);
    message.setSubject(subject);
    message.setText(body);
    emailSender.send(message);
  }

  @Override
  public boolean confirm(String token) {
    Proposal proposal = proposalRepository.findById(token).orElseThrow(
        () -> new TokenNotFoundException("Token " + token + " does not exists"));
    if (!proposal.isValid()) {
      return false;
    }

    if(proposal.getStatus() == ProposalStatus.PENDING) {
      proposal.setStatus(ProposalStatus.ACCEPTED);
      proposalRepository.save(proposal);

      // Reject all pending proposals since this one is accepted 100%
      proposalRepository.findAllByInvitedUserIdAndCourseId(proposal.getInvitedUserId(), proposal.getCourseId()).stream()
          .filter(p -> p.getStatus().equals(ProposalStatus.PENDING) && p.isValid())
          .forEach(p -> reject(p.getId()));

      if (proposalRepository.findAllByTeamId(proposal.getTeamId()).stream()
          .allMatch(p -> p.getStatus().equals(ProposalStatus.ACCEPTED))) {
        //If they are all in the accepted state I delete all the proposals and confirm the associated team
        teamService.activateTeam(proposal.getTeamId());
        teamService.getTeamMembers(proposal.getTeamId())
            .forEach(m -> {
              // Delete all proposals since the members are now officially part of a team
              proposalRepository.findAllByInvitedUserIdAndCourseId(m.getId(), proposal.getCourseId())
                  .stream().filter(p -> !Utility.isProposalDeleted(p.getStatus()))
                  .forEach(p -> deleteProposal(p.getId()));
            });
        proposalRepository.deleteAll(proposalRepository.findAllByTeamId(proposal.getTeamId()));
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean reject(String token) {
    Proposal proposal = proposalRepository.findById(token).orElseThrow(
        () -> new TokenNotFoundException("Token " + token + " does not exists"));
    if (!proposal.isValid()) {
      return false;
    }

    proposalRepository.findAllByTeamId(proposal.getTeamId()).forEach(p -> p.setValid(false));
    proposal.setStatus(ProposalStatus.REJECTED);
    proposalRepository.save(proposal);
    return true;
  }

  @Override
  public boolean deleteProposal(String token) {
    Proposal proposal = proposalRepository.findById(token).orElseThrow(
        () -> new TokenNotFoundException("Token " + token + " does not exists"));
    if (proposal.isValid()) {
      return false;
    }

    proposal.setStatus(Utility.deletedStatusOf(proposal.getStatus()));
    proposalRepository.save(proposal);

    //If they are all in the deleted state I delete all the proposals and the associated team
    List<Proposal> proposalList = proposalRepository.findAllByTeamId(proposal.getTeamId());
    if (proposalList.stream().allMatch(p -> Utility.isProposalDeleted(p.getStatus()))) {
      proposalRepository.deleteAll(proposalList);
      teamService.evictTeam(proposal.getTeamId());
      return true;
    }
    return false;
  }

  @Override
  public void notifyTeam(TeamDTO dto, List<String> memberIds, String courseId) {
    String members = String.join("\n- ", memberIds);
    String url = "https://localhost:4200/student/courses/" + courseId + "/teams";

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
}
