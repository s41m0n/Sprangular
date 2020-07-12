package it.polito.ai.lab2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Professor extends User {

    @OneToMany(mappedBy = "professor")
    List<Course> professorCourses;

    public void addCourse(Course c) {
        professorCourses.add(c);
        c.setProfessor(this);
    }

    public void removeCourse(Course c) {
        professorCourses.remove(c);
        c.setProfessor(null);
    }
}
