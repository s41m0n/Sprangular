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

    String name;

    String acronym;

    int teamMinSize;

    int teamMaxSize;

    boolean enabled;

    @ManyToMany(mappedBy = "courses")
    List<Professor> professors = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    List<Team> teams = new ArrayList<>();

    @OneToOne(mappedBy = "course")
    VmModel vmModel;

    @OneToMany(mappedBy = "course")
    List<Assignment> assignments = new ArrayList<>();
}
