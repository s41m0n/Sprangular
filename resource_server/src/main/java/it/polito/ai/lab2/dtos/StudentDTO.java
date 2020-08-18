package it.polito.ai.lab2.dtos;

import com.opencsv.bean.CsvBindByName;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    @CsvBindByName
    String email;

    @NotNull
    @NotEmpty
    @CsvBindByName
    String id;

    @CsvBindByName
    String name;

    @CsvBindByName
    String surname;

    @CsvBindByName
    String photoPath;
}
