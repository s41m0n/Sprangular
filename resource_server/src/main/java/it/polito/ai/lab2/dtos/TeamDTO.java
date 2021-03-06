package it.polito.ai.lab2.dtos;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDTO extends RepresentationModel<TeamDTO> {

  @NotNull
  @NotEmpty
  Long id;

  @NotNull
  @NotEmpty
  String name;

  boolean active;

  int maxVCpu;

  int maxDiskStorage;

  int maxRam;

  int maxActiveInstances;

  int maxTotalInstances;
}
