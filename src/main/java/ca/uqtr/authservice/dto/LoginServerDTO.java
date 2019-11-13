package ca.uqtr.authservice.dto;

import lombok.ToString;

@ToString
public class LoginServerDTO {

    private Boolean userNameExist=false;
    private Boolean passwordIsTrue=false;

    public LoginServerDTO() {
    }

    public void isUserNameExist(Boolean userNameExist) {
        this.userNameExist = userNameExist;
    }

    public void isPasswordIsTrue(Boolean passwordIsTrue) {
        this.passwordIsTrue = passwordIsTrue;
    }
}
