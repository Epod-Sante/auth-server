package ca.uqtr.authservice.dto;

import ca.uqtr.authservice.entity.Permission;
import ca.uqtr.authservice.entity.Role;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class LoginServerDTO {

    private String username;
    private Role roles;
    private List<Permission> permissions;
    private String access_token;
    private HttpStatus status;
    private Boolean userNameExist=false;
    private Boolean passwordIsTrue=false;
    private Boolean compteEnabled;


}
