package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    String email;

    String password;

    @Column(unique = true)
    String id;

    String name;

    String surname;

    String photoPath;

    boolean verified;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }
}
