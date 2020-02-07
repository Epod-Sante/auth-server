package ca.uqtr.authservice.controller;


import ca.uqtr.authservice.service.AccountService;
import ca.uqtr.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PatientController {


    private AccountService accountService;
    private UserService userService;

    @Autowired
    public PatientController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }



}
