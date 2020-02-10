package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.dto.UserResponseDto;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.service.AccountService;
import ca.uqtr.authservice.service.UserService;
import ca.uqtr.authservice.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class UserController {

    private Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountService accountService;
    private final UserService userService;

    @Autowired
    public UserController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostMapping(value = "/user/invite")
    public UserInviteDto userInvite(@RequestBody String inviteUserDto) throws IOException {
        return accountService.userInvite(inviteUserDto);
    }

    @GetMapping("/user/invite")
    public UserInviteDto userInviteToken(@RequestParam("token") String token)  {
        UserInviteDto userInviteDto = new UserInviteDto();
        Account account = accountService.getUserInviteToken(token);
        if (account == null) {
            userInviteDto.setTokenExist(false);
            userInviteDto.setErrorMessage("Invalid token.");
            return userInviteDto;
        }
        userInviteDto.setTokenExist(true);
        Calendar cal = Calendar.getInstance();
        long time = cal.getTime().getTime() - (account.getInviteTokenExpirationDate().getTime() + TimeUnit.MINUTES.toMillis(60));
        if (time > 0) {
            userInviteDto.setTokenExpired(true);
            userInviteDto.setErrorMessage("Your invitation token has expired. Contact your admin for mor information.");
            return userInviteDto;
        }
        userInviteDto.setTokenExpired(false);
        userInviteDto.setEmail(account.getUser().getEmail().getValue());
        System.out.println(userInviteDto.toString());
        return userInviteDto;
    }

    @PostMapping("/create/user")
    public ResponseEntity<UserResponseDto> registration(@RequestBody String registrationClientDTO) {
        System.out.println("//////////////////////////////////"+registrationClientDTO);
        UserResponseDto registration = new UserResponseDto();
        try {
            registration = accountService.createAccount(registrationClientDTO);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised registration REST Call", ex);
            return new ResponseEntity<>(registration, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }

    @GetMapping("/user/all")
    public List<UserRequestDto> usersList(HttpServletRequest request)  {
        String token = request.getHeader("Authorization").replace("bearer ","");
        return userService.usersList(JwtTokenUtil.getUsername(token));
    }

    @PutMapping("/update/user/enable")
    public void enableUser(@RequestBody String userRequestDto, @RequestParam boolean enable) throws IOException {
        userService.enableUser(userRequestDto, enable);
    }



}
