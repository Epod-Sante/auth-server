package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.dto.model.RoleDto;
import ca.uqtr.authservice.entity.Account;

import java.text.ParseException;

public interface AccountService {

    LoginServerDTO loadAccount(LoginClientDTO loginClientDTO);

    RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO) throws ParseException;

    void createVerificationToken(RegistrationClientDTO user, String token);

    Account getVerificationToken(String VerificationToken);

    void updateAccount(Account account);

    void setRoleToUser(RoleDto roleDto);
}
