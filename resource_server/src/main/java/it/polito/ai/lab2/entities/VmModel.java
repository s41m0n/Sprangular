package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class VmModel {

  @Id
  @GeneratedValue
  Long id;

  String name;

  @OneToOne
  @JoinColumn(name = "team_id")
  Team team;

  @ManyToOne
  @JoinColumn(name = "course_id")
  Course course;

  String imagePath;
}
