package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CourseWithModelDetails {
  String acronym;
  String name;
  int teamMinSize;
  int teamMaxSize;
  boolean enabled;
  MultipartFile vmModel;
}
