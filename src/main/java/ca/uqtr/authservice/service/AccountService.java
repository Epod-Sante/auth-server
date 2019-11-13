package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;

public interface AccountService {

    LoginServerDTO loadAccount(LoginClientDTO loginClientDTO);

    RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO);


}
