package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String name;

    String acronym;

    int teamMinSize;

    int teamMaxSize;

    boolean enabled;

    @ManyToMany(mappedBy = "courses")
    List<Professor> professors = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "course_student", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    List<Team> teams = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "vmModel_id")
    VmModel vmModel;

    @OneToMany(mappedBy = "course")
    List<Assignment> assignments = new ArrayList<>();
}
