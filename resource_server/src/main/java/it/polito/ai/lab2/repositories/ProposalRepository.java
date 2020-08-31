package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, String> {

    List<Proposal> findAllByExpiryDateAfter(Timestamp t);

    List<Proposal> findAllByTeamId(Long teamId);

    List<Proposal> findAllByInvitedUserIdAndCourseId(String userId, String courseId);

}
