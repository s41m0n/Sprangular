package it.polito.ai.lab2.dtos;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfessorDTO extends RepresentationModel<ProfessorDTO> {

    String email;

    @NotNull
    @NotEmpty
    String id;

    String name;

    String surname;

    String photoPath;
}
