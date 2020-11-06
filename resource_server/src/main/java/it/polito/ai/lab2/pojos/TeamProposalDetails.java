package it.polito.ai.lab2.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamProposalDetails {
  String token; // Used to generate the clickable on the frontend
  String proposalCreator;
  String teamName;
  List<String> membersAndStatus;
  Timestamp deadline;
  boolean valid;
}
