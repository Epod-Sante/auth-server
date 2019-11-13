package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/account")
public class AccountController {
    protected Logger logger = Logger.getLogger(AccountController.class.getName());

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * login.
     * *
     *
     * @param loginClientDTO
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginServerDTO> login(@RequestBody LoginClientDTO loginClientDTO){
        LoginServerDTO login = new LoginServerDTO();
        try {
            login = accountService.loadAccount(loginClientDTO);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised sign in REST Call", ex);
            return new ResponseEntity<>(login, HttpStatus.UNAUTHORIZED);
        }
        System.out.println(login.toString());
        return new ResponseEntity<>(login, HttpStatus.OK);
    }


    /**
     * registration.
     * *
     *
     * @param registrationClientDTO
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationServerDTO> registration(@RequestBody RegistrationClientDTO registrationClientDTO){

        RegistrationServerDTO registration = new RegistrationServerDTO();
        try {
            registration = accountService.saveAccount(registrationClientDTO);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised registration REST Call", ex);
            return new ResponseEntity<>(registration, HttpStatus.BAD_REQUEST);
        }
        System.out.println(registration.toString());
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }
}
