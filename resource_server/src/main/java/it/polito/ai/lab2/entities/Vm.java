package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Vm {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "vmModel_id")
    VmModel vmModel;

    int vCpu;

    int diskStorage;

    int ram;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "vm_owner", joinColumns = @JoinColumn(name = "vm_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    List<Student> owners = new ArrayList<>();

    boolean active;

    String imagePath;
}
