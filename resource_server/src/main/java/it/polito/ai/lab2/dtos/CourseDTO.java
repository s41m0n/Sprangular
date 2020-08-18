package it.polito.ai.lab2.dtos;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @Id
    Long id;

    @NotNull
    @NotEmpty
    String name;

    String acronym;

    @Min(0)
    int teamMinSize;

    @Min(0)
    int teamMaxSize;

    boolean enabled;
}
