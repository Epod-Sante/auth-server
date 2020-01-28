package ca.uqtr.authservice.dto;

import lombok.ToString;

@ToString
public class RegistrationServerDTO {

    private Boolean emailExist=false;
    private Boolean usernameExist=false;
    private Boolean isSignup=false;

    public RegistrationServerDTO() {
    }

    public void isEmailExist(Boolean emailExist) {
        this.emailExist = emailExist;
    }

    public void isUsernameExist(Boolean usernameExist) {
        this.usernameExist = usernameExist;
    }

    public void isRegistered(Boolean signup) {
        this.isSignup = signup;
    }

    public Boolean isEmailExist() {
        return emailExist;
    }

    public Boolean isUsernameExist() {
        return usernameExist;
    }
}
