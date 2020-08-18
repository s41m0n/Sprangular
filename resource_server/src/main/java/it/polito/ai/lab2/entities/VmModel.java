package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class VmModel {

  @Id
  @GeneratedValue
  Long id;

  String name;

  @OneToOne(mappedBy = "vmModel")
  Course course;

  String imagePath;
}
