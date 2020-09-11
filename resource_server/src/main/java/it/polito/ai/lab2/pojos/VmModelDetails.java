package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VmModelDetails {

  String name;

  MultipartFile image;
}
