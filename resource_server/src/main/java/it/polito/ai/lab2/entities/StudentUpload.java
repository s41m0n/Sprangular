package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class StudentUpload {

    @Id
    @GeneratedValue
    Long id;

    String imagePath;

    Timestamp timestamp;

    String comment;

    @ManyToOne
    @JoinColumn(name = "assignmentSolution_id")
    AssignmentSolution assignmentSolution;

    @OneToOne
    @JoinColumn(name = "teacherUpload_id")
    ProfessorUpload teacherRevision;
}
