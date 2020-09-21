package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Role {

  @Id
  @GeneratedValue
  Long id;

  String name;

  @ManyToMany(mappedBy = "roles")
  List<User> users = new ArrayList<>();
}
