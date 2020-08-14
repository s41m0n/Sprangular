package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import javax.persistence.*;
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

  @OneToMany
  @Column(name = "upload_id")
  List<StudentUpload> studentUploads = new ArrayList<>();

  int grade;
}