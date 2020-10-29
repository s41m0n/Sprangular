package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class AssignmentSolution {

  @Id
  @GeneratedValue
  Long id;

  @ManyToOne
  @JoinColumn(name = "assignment_id")
  Assignment assignment;

  @ManyToOne
  @JoinColumn(name = "student_id")
  Student student;

  @Enumerated(EnumType.STRING)
  AssignmentStatus status;

  @OneToMany(mappedBy = "assignmentSolution")
  List<StudentUpload> studentUploads = new ArrayList<>();

  String grade;

  Timestamp statusTs;
}
