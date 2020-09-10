package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.utility.ProposalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@NoArgsConstructor
public class TeamProposalDetails {

    String token; //ci serve per creare il link cliccabile nel frontend

    Student proposalCreator;

    String teamName;

    Map<Student, ProposalStatus> membersAndStatus;

    Timestamp deadline;
}
