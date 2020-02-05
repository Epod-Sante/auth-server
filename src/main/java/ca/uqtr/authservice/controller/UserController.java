package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class UserController {

    private Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountService accountService;
    @Resource(name = "tokenServices")
    private ConsumerTokenServices tokenServices;
    @Resource(name = "jdbcTokenStore")
    private TokenStore tokenStore;

    @Autowired
    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = "/user/invite")
    public void userInvite(@RequestBody String inviteUserDto) throws IOException {
        accountService.userInvite(inviteUserDto);
    }

    @GetMapping("/user/invite")
    public UserInviteDto userInviteToken(@RequestParam("token") String token)  {
        UserInviteDto userInviteDto = new UserInviteDto();
        Account account = accountService.getUserInviteToken(token);
        userInviteDto.setEmail(account.getUser().getEmail().getValue());
        if (account == null) {
            userInviteDto.setErrorMessage("Invalid token.");
            return userInviteDto;
        }
        userInviteDto.setTokenExist(true);
        if (account.isEnabled()) {
            userInviteDto.setErrorMessage("You are registered.");
            return userInviteDto;
        }
        Calendar cal = Calendar.getInstance();
        long time = cal.getTime().getTime() - (account.getInviteTokenExpirationDate().getTime() + TimeUnit.MINUTES.toMillis(60));
        if (time > 0) {
            userInviteDto.setErrorMessage("Your invitation token has expired....!!");
            return userInviteDto;
        }
        //account.isEnabled(true);
        //accountService.updateAccount(account);
        userInviteDto.setErrorMessage("Your can register now.");
        return userInviteDto;
    }

    @PostMapping("/user/registration")
    public ResponseEntity<RegistrationServerDTO> registration(@RequestBody String registrationClientDTO) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RegistrationClientDTO rcDto = mapper.readValue(registrationClientDTO, RegistrationClientDTO.class);
        System.out.println("//////////////////////////////////"+rcDto.toString());
        RegistrationServerDTO registration = new RegistrationServerDTO();
        try {
            registration = accountService.saveAccount(rcDto);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised registration REST Call", ex);
            return new ResponseEntity<>(registration, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }

}
