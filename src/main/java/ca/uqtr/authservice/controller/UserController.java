package ca.uqtr.authservice.controller;

import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.dto.UserResponseDto;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.service.AccountService;
import ca.uqtr.authservice.service.UserService;
import ca.uqtr.authservice.utils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final WebClient webClient;
    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    public UserController(AccountService accountService, UserService userService, WebClient webClient){
        this.accountService = accountService;
        this.userService = userService;
        this.webClient = webClient;
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

    @PostMapping("/user/create")
    public ResponseEntity<UserResponseDto> registration(HttpServletRequest request, @RequestBody String registrationClientDTO) throws JsonProcessingException {
        String token = request.getHeader("Authorization").replace("bearer ","");
        ObjectMapper mapper = new ObjectMapper();
        UserRequestDto userRequestDto = mapper.readValue(registrationClientDTO, UserRequestDto.class);
        System.out.println("//////////////////////////////////"+registrationClientDTO);
        UserResponseDto registration = new UserResponseDto();
        try {
            registration = accountService.createAccount(registrationClientDTO);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception raised registration REST Call", ex);
            return new ResponseEntity<>(registration, HttpStatus.BAD_REQUEST);
        }
        String url;
        if (profile.equals("dev"))
            url = "http://localhost:8762/api/v1/patient-service/create/professional";
        else
            url = "https://epod-zuul.herokuapp.com/api/v1/patient-service/create/professional";
        this.webClient
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(userRequestDto), UserRequestDto.class);

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

    @PostMapping("/user")
    public UserRequestDto userInfos(@RequestParam String username) {
        return userService.getUserInfos(username);
    }

}
