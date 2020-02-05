package ca.uqtr.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInviteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String adminUsername;
    private String email;
    private String role;
    private Boolean tokenExist;
    private Boolean emailExist;
    private String errorMessage;
}
