package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadDetails {
  String comment;
  MultipartFile document;
  boolean reUploadable;
}
