package it.polito.ai.lab2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Professor extends User {

  @ManyToMany(mappedBy = "professors")
  List<Course> courses = new ArrayList<>();

  @OneToMany(mappedBy = "professor")
  List<Assignment> assignments = new ArrayList<>();

  public boolean addCourse(Course course) {
    if (!this.courses.contains(course)) {
      this.courses.add(course);
      course.getProfessors().add(this);
      return true;
    }
    return false;
  }

  public void removeCourse(Course course) {
    if (this.courses.contains(course)) {
      this.courses.remove(course);
      course.getProfessors().remove(this);
    }
  }
}
