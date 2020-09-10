package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.utility.ProposalStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.sql.Timestamp;


@Entity
@Data
public class Proposal {

    @Id
    String id; //= token

    String proposalCreatorId;

    String invitedUserId;

    Long teamId;

    String courseId;

    Timestamp deadline;

    @Enumerated(EnumType.STRING)
    ProposalStatus status;
}
