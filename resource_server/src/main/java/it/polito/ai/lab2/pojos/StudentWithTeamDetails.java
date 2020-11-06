package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.dtos.TeamDTO;
import lombok.Data;

@Data
public class StudentWithTeamDetails {
  String email;
  String id;
  String name;
  String surname;
  TeamDTO team;
}
