package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.event.OnRegistrationCompleteEvent;
import ca.uqtr.authservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping
public class AccountController {
    private Logger logger = Logger.getLogger(AccountController.class.getName());
    private ApplicationEventPublisher eventPublisher;
    private AccountService accountService;
    @Resource(name = "tokenServices")
    private ConsumerTokenServices tokenServices;
    @Resource(name = "jdbcTokenStore")
    private TokenStore tokenStore;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public AccountController(@Qualifier("accountServiceAuth") AccountService accountService, ApplicationEventPublisher eventPublisher) {
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * login.
     * *
     *
     * @param
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    /*@PostMapping("/login")
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
    }*/
    @PostMapping("/login")
    public ResponseEntity<LoginServerDTO> login(@RequestBody LoginClientDTO loginClientDTO) {

        String username = loginClientDTO.getUsername();
        String password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(loginClientDTO.getPassword());

        //System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
        LoginServerDTO response = this.accountService.loadAccount(loginClientDTO);
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
     * @param registrationClientDTO
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationServerDTO> registration(@RequestBody String registrationClientDTO,
                                                              HttpServletRequest request) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        RegistrationClientDTO rcDto = mapper.readValue(registrationClientDTO, RegistrationClientDTO.class);

        System.out.println("//////////////////////////////////"+rcDto.toString());
        RegistrationServerDTO registration = new RegistrationServerDTO();
        try {
            registration = accountService.saveAccount(rcDto);
            System.out.println(registration.toString());
            boolean email = registration.isEmailExist();
            boolean user = registration.isUsernameExist();
            boolean pr = registration.isProfileIsSet();
            System.out.println(email);
            System.out.println(user);
            System.out.println(pr);

            if (email || user){
                String appUrl = request.getContextPath();
                System.out.println(appUrl);
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(rcDto, appUrl));
            }

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
    @DeleteMapping("/loggingout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String token = authorization.substring("Bearer".length() + 1);
            tokenServices.revokeToken(token);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/registrationConfirm")
    public ResponseEntity<String> registrationConfirm(@RequestParam("token") String token) {
        Account account = accountService.getVerificationToken(token);
        if (account == null) {
            String message = "Invalid token.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        Calendar cal = Calendar.getInstance();
        if ((account.getVerificationTokenExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "Your registration token has expired....!!";
            return new ResponseEntity<>(messageValue, HttpStatus.BAD_REQUEST);
        }

        account.setEnabled(true);
        accountService.updateAccount(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
