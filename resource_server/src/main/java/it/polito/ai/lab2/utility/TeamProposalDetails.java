package it.polito.ai.lab2.utility;

import it.polito.ai.lab2.entities.Student;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class TeamProposalDetails {

    Student proposalCreator;

    String teamName;

    Map<Student, ProposalStatus> membersAndStatus;
}
