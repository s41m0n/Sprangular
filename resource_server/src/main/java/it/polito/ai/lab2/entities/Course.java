package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

  @Id
  String acronym;

  @Column(unique = true)
  String name;

  int teamMinSize;

  int teamMaxSize;

  boolean enabled;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "professor_course", joinColumns = @JoinColumn(name = "course_acronym"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
  List<Professor> professors = new ArrayList<>();

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "course_student", joinColumns = @JoinColumn(name = "course_acronym"), inverseJoinColumns = @JoinColumn(name = "student_id"))
  List<Student> students = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  List<Team> teams = new ArrayList<>();

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "vmModel_id")
  VmModel vmModel;

  @OneToMany(mappedBy = "course")
  List<Assignment> assignments = new ArrayList<>();

  public void addProfessor(Professor professor) {
    professor.addCourse(this);
  }

  public void removeProfessor(Professor professor) {
    professor.removeCourse(this);
  }
}
