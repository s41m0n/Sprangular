package it.polito.ai.lab2.pojos;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TeamProposalRequest {

  String teamName;

  List<String> studentIds;

  Timestamp deadline;
}
