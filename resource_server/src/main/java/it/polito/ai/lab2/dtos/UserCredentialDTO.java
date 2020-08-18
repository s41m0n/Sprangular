package it.polito.ai.lab2.dtos;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserCredentialDTO {

    @NotNull
    @NotEmpty
    String email;

    @NotNull
    @NotEmpty
    String saltedPassword;

    String salt;
}
