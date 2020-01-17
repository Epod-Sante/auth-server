package ca.uqtr.authservice.dto;

import lombok.ToString;

@ToString
public class RegistrationServerDTO {

    private Boolean emailExist=false;
    private Boolean usernameExist=false;
    private Boolean profileIsSet =false;
    private Boolean isSignup=false;

    public RegistrationServerDTO() {
    }

    public void isEmailExist(Boolean usernameExist) {
        this.emailExist = emailExist;
    }

    public void isUsernameExist(Boolean usernameExist) {
        this.usernameExist = usernameExist;
    }

    public void isProfileIsSet(Boolean professionIsSet) {
        this.profileIsSet = professionIsSet;
    }

    public void isRegistred(Boolean signup) {
        isSignup = signup;
    }

    public Boolean isEmailExist() {
        return emailExist;
    }

    public Boolean isProfileIsSet() {
        return profileIsSet;
    }

    public Boolean isUsernameExist() {
        return usernameExist;
    }
}
