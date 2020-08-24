package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String name;

    int status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "team_id"))
    List<Student> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    List<Vm> vms = new ArrayList<>();

    int maxVCpu;

    int maxDiskStorage;

    int maxRam;

    int maxActiveInstances;

    int maxTotalInstances;
}
