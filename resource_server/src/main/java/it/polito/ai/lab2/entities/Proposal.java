package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.utility.ProposalStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;


@Entity
@Data
public class Proposal {

    @Id
    String id;

    String proposalCreatorId;

    String invitedUserId;

    Long teamId;

    Timestamp expiryDate;

    ProposalStatus status;

    //TODO: decidere come far a mandare al frontend tutte le info che devono essere mostrate nella pagina.
}
