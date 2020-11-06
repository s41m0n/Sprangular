package it.polito.ai.lab2.pojos;

import lombok.Data;

import java.util.List;

@Data
public class TeamProposalRequest {
  String teamName;
  List<String> studentIds;
  Long deadline; //Unix timestamp
}
