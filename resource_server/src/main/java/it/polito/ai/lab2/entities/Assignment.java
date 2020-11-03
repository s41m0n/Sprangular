package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Assignment {

  @Id
  @GeneratedValue
  Long id;

  String name;

  String imagePath;

  @ManyToOne
  @JoinColumn(name = "course_acronym")
  Course course;

  @ManyToOne
  @JoinColumn(name = "professor_id")
  Professor professor;

  @OneToMany(mappedBy = "assignment")
  List<AssignmentSolution> solutions = new ArrayList<>();

  Timestamp releaseDate;

  Timestamp dueDate;
}
