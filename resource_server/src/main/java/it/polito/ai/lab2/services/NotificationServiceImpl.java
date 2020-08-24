package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Proposal;
import it.polito.ai.lab2.repositories.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

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
        message.setTo(address);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Override
    public boolean confirm(String token) {
        Proposal t = proposalRepository.findById(token).orElse(null);

        if (t == null || t.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) return false;

        proposalRepository.delete(t);

        if (!proposalRepository.findAllByTeamId(t.getTeamId()).isEmpty()) return false;

        teamService.activeTeam(t.getTeamId());
        return true;
    }

    @Override
    public boolean reject(String token) {
        Proposal t = proposalRepository.findById(token).orElse(null);
        if (t == null || t.getExpiryDate().before(new Timestamp(System.currentTimeMillis())))
            return false;

        proposalRepository.deleteAll(proposalRepository.findAllByTeamId(t.getTeamId()));
        teamService.evictTeam(t.getTeamId());
        return true;
    }

    @Override
    @Transactional
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {
        Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 3600000);
        String members = String.join("\n- s", memberIds);

        memberIds.remove(SecurityContextHolder.getContext().getAuthentication().getName());
        memberIds.forEach(memberId -> {
            Proposal proposal = new Proposal();
            proposal.setExpiryDate(expiryDate);
            proposal.setId((UUID.randomUUID().toString()));
            proposal.setTeamId(dto.getId());

            proposalRepository.save(proposal);

            String email = "s" +  memberId + "@studenti.polito.it";
            String confirm = "http://localhost:8080/API/notification/confirm/" + proposal.getId();
            String reject = "http://localhost:8080/API/notification/reject/" + proposal.getId();
            sendMessage(email, "[SpringExample] Someone wants to create a team with you",
                    "Dear " + email + ",\n\n" +
                            "You have been requested to create the team `" + dto.getName() +"`.\n" +
                    "The members would be:\n- s" + members +
                    "\n\nIf you are willing to confirm, visit the url ->" + confirm +
                    "\nOtherwise, to reject visit -> " + reject +
                    "\n\nBest Regards,\nSpringExample Team");
        });
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void fixedTokenClear() {
        Set<Long> teamIds = new HashSet<>();
        proposalRepository.findAllByExpiryDateAfter(new Timestamp(System.currentTimeMillis())).forEach(proposal -> {
            teamIds.add(proposal.getTeamId());
            proposalRepository.delete(proposal);
        });
        teamIds.forEach(team -> teamService.evictTeam(team));
    }
}
