package ca.uqtr.authservice.dto;

import lombok.ToString;

@ToString
public class RegistrationServerDTO {

    private Boolean institutionExist=false;
    private Boolean emailExist=false;
    private Boolean usernameExist=false;

    public RegistrationServerDTO() {
    }

    public void isEmailExist(Boolean emailExist) {
        this.emailExist = emailExist;
    }

    public void isUsernameExist(Boolean usernameExist) {
        this.usernameExist = usernameExist;
    }

    public Boolean isEmailExist() {
        return emailExist;
    }

    public Boolean isUsernameExist() {
        return usernameExist;
    }

    public Boolean isInstitutionExist() {
        return institutionExist;
    }

    public void isInstitutionExist(Boolean institutionExist) {
        this.institutionExist = institutionExist;
    }
}
