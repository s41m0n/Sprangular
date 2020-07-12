package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    String name;

    int min;

    int max;

    boolean enabled;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    Professor professor;

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    List<Team> teams;

    public void addStudent(Student student) {
        students.add(student);
        student.getCourses().add(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.getCourses().remove(this);
    }

    public void addTeam(Team team) {
        teams.add(team);
        team.setCourse(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.setCourse(null);
    }
}
