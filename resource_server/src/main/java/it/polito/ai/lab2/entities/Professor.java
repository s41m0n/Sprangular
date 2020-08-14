package it.polito.ai.lab2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Professor extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "professor_course", joinColumns = @JoinColumn(name = "professor_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "professor")
    List<Assignment> assignments = new ArrayList<>();
}
