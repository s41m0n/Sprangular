package it.polito.ai.lab2.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamProposalDetails {

  String token; //ci serve per creare il link cliccabile nel frontend

  String proposalCreator;

  String teamName;

  List<String> membersAndStatus;

  Timestamp deadline;

  boolean valid;
}
