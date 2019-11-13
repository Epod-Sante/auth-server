package ca.uqtr.authservice.dto;

import lombok.ToString;

@ToString
public class RegistrationServerDTO {

    private Boolean emailExist=false;
    private Boolean professionIsSet=false;
    private Boolean isSignup=false;

    public RegistrationServerDTO() {
    }

    public void isEmailExist(Boolean emailExist) {
        this.emailExist = emailExist;
    }

    public void isProfessionIsSet(Boolean professionIsSet) {
        this.professionIsSet = professionIsSet;
    }

    public void isRegistred(Boolean signup) {
        isSignup = signup;
    }
}
