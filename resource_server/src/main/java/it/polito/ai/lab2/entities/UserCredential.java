package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserCredential {

  @Id
  String email;

  @OneToOne
  @MapsId
  User user;

  String saltedPassword;

  String salt;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  List<Role> roles = new ArrayList<>();
}
