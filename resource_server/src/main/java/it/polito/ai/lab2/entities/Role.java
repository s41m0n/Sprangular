package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
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
    List<UserCredential> users = new ArrayList<>();
}
