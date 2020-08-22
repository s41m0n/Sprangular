package it.polito.ai.lab2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Student extends User {

    @ManyToMany(mappedBy = "students")
    List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    List<Team> teams = new ArrayList<>();

    @ManyToMany(mappedBy = "owners")
    List<Vm> ownedVms = new ArrayList<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }
}
