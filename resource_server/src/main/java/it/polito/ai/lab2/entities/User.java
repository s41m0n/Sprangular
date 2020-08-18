package it.polito.ai.lab2.entities;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class User {

    @Id
    String email;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    UserCredential userCredential;

    @Column(unique = true)
    String id;

    String name;

    String surname;

    String photoPath;
}
