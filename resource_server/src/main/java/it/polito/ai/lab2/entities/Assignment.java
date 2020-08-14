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
  @JoinColumn(name = "course_id")
  Course course;

  @ManyToOne
  @JoinColumn(name = "professor_id")
  Professor professor;

  @OneToMany
  List<AssignmentSolution> solutions = new ArrayList<>();

  Timestamp releaseDate;

  Timestamp dueDate;
}
