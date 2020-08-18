package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class VmModel {

  @Id
  @GeneratedValue
  Long id;

  String name;

  @OneToOne(mappedBy = "vmModel")
  Course course;

  @OneToMany(mappedBy = "vmModel")
  List<Vm> vms = new ArrayList<>();

  String imagePath;
}
