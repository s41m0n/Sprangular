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

    String name;

    int status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "team_id"))
    List<Student> members = new ArrayList<>();

    public void addMember(Student student) {
        this.members.add(student);
        student.getTeams().add(this);
    }

    public void removeMember(Student student) {
        this.members.remove(student);
        student.getTeams().remove(this);
    }

    public void setCourse(Course course) {
        if(this.course != null) this.course.getTeams().remove(this);
        this.course = course;
        if(course != null) course.teams.add(this);
    }
}
