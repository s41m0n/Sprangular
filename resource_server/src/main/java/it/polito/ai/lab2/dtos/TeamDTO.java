package it.polito.ai.lab2.dtos;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDTO  extends RepresentationModel<TeamDTO> {

    @Id
    @NotNull
    @NotEmpty
    Long id;

    @NotNull
    @NotEmpty
    String name;

    int status;

    int maxVCpu;

    int maxDiskStorage;

    int maxRam;

    int maxActiveInstances;

    int maxTotalInstances;
}
