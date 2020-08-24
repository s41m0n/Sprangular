package it.polito.ai.lab2.utility;

import it.polito.ai.lab2.entities.Student;

import java.util.Map;

public class TeamProposalDetails {

    Student proposalCreator;

    String teamName;

    Map<Student, ProposalStatus> membersAndStatus;
}
