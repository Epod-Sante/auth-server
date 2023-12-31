package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.*;
import ca.uqtr.authservice.dto.model.RoleDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Permission;
import ca.uqtr.authservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping
public class AccountController {
    private Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountService accountService;
    @Resource(name = "tokenServices")
    private ConsumerTokenServices tokenServices;
    @Resource(name = "jdbcTokenStore")
    private TokenStore tokenStore;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * login.
     * *
     *
     * @param
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginServerDTO> login(@RequestBody String loginClientDTO) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        LoginClientDTO lcDto = mapper.readValue(loginClientDTO, LoginClientDTO.class);

        String username = lcDto.getUsername();
        String password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(lcDto.getPassword());

        //System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
        LoginServerDTO response = this.accountService.loadAccount(lcDto);
        System.out.println(response);
        try {
            //https://www.baeldung.com/manually-set-user-authentication-spring-security
            //https://www.baeldung.com/spring-security-extra-login-fields

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String access = tokenStore.getAccessToken((OAuth2Authentication) authentication).getValue();
            response.setAccess_token(access);
            System.out.println(access);
            response.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException bce) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            return new ResponseEntity<>(new LoginServerDTO(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * registration.
     * *
     *
     * @param userRequestDto
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> registration(@RequestBody String userRequestDto) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserRequestDto rcDto = mapper.readValue(userRequestDto, UserRequestDto.class);
        UserResponseDto registration = new UserResponseDto();
        try {
            registration = accountService.saveAccount(rcDto);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised registration REST Call", ex);
            return new ResponseEntity<>(registration, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(registration, HttpStatus.OK);
    }

    /**
     * logout.
     * *
     *
     * @param request
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @DeleteMapping("/logingout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").replace("bearer ","");
        //tokenServices.revokeToken(authorization);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/registration/confirm")
    public ResponseEntity<String> registrationConfirm(@RequestParam("token") String token) {
        Account account = accountService.getRegistrationVerificationToken(token);
        if (account == null) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.OK);
        }
        if (account.isEnabled()) {
            return new ResponseEntity<>("Your account is active.", HttpStatus.OK);
        }
        Calendar cal = Calendar.getInstance();
        long time = cal.getTime().getTime() - (account.getVerificationTokenExpirationDate().getTime() + TimeUnit.MINUTES.toMillis(1));
        if (time > 0) {
            accountService.updateRegistrationVerificationToken(account.getUser().getEmail().getValue());
            return new ResponseEntity<>("Your registration token has expired....!!. We did send you another e-mail.", HttpStatus.BAD_REQUEST);
        }

        account.isEnabled(true);
        accountService.updateAccount(account);
        return new ResponseEntity<>("Your account is activated.", HttpStatus.OK);
    }

    /**
     * addRole.
     * *
     *
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PutMapping("/user/add/role")
    public ResponseEntity<HttpStatus> addRoleToUser(@RequestBody String roleDto)  {
        try {
            System.out.println(roleDto);
            ObjectMapper mapper = new ObjectMapper();
            RoleDto role = mapper.readValue(roleDto, RoleDto.class);
            accountService.addRoleToUser(role);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
//************************************************************************************************************
    /**
     * addRole.
     * *
     *
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PutMapping("/add/role")
    public ResponseEntity<HttpStatus> addRole(@RequestBody String roleDto)  {
        try {
            System.out.println(roleDto);
            ObjectMapper mapper = new ObjectMapper();
            RoleDto role = mapper.readValue(roleDto, RoleDto.class);
            accountService.addRoleToUser(role);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * addPermission.
     * *
     *
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/insert/permission")
    public ResponseEntity<HttpStatus> addPermission(@RequestBody String permissionDto)  {

        return null;

    }

    @GetMapping("/permission/all")
    public Iterable<Permission> getAllPermissions()  {
        return accountService.getAllPermissions();
    }

    @PostMapping("/update/password/mail")
    public PasswordUpdateDto passwordUpdateMail(@RequestBody String passwordUpdateDto) throws IOException {
        return accountService.updatePasswordGetURL(passwordUpdateDto);
    }

    @GetMapping("/update/password")
    public PasswordUpdateDto passwordUpdateToken(@RequestParam("token") String token)  {
        PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto();
        Account account = accountService.getUpdatePasswordToken(token);
        passwordUpdateDto.setEmail(account.getUser().getEmail().getValue());
        if (account == null) {
            passwordUpdateDto.setErrorMessage("Invalid token.");
            return passwordUpdateDto;
        }
        passwordUpdateDto.setTokenExist(true);
        if (account.isEnabled()) {
            passwordUpdateDto.setErrorMessage("Your account is active.");
            return passwordUpdateDto;
        }
        Calendar cal = Calendar.getInstance();
        long time = cal.getTime().getTime() - (account.getResetPasswordTokenExpirationDate().getTime() + TimeUnit.MINUTES.toMillis(60));
        if (time > 0) {
            passwordUpdateDto.setErrorMessage("Your update password token has expired....!!");
            return passwordUpdateDto;
        }
        account.isEnabled(true);
        accountService.updateAccount(account);
        passwordUpdateDto.setErrorMessage("Your password is updated");
        return passwordUpdateDto;
    }

    @PostMapping("/update/password")
    public void passwordUpdate(@RequestParam String token, @RequestParam String password)  {
        accountService.updatePassword(token, password);
    }
}
