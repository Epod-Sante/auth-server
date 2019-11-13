package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping
public class AccountController {
    protected Logger logger = Logger.getLogger(AccountController.class.getName());

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

    /**
     * logout.
     * *
     *
     * @param request
     * @return A bool.
     * @throws Exception If there are no matches at all.
     */
    @DeleteMapping("/loggingout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")){
            String token = authorization.substring("Bearer".length()+1);
            tokenServices.revokeToken(token);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
