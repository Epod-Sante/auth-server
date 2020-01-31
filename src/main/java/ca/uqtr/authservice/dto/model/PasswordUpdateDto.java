package ca.uqtr.authservice.dto.model;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String email;
    private String password;
    private Boolean accountEnabled;
    private Boolean emailExist;
    private Boolean passwordUpdated;
}
