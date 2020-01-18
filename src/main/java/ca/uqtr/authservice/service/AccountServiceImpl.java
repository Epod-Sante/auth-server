package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("accountServiceAuth")
public class AccountServiceImpl implements AccountService, UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Override
    public LoginServerDTO loadAccount(LoginClientDTO loginClientDTO) {
//        Account account = new Account();
//        BeanUtils.copyProperties(signinDTO, account);

        LoginServerDTO loginServerDTO = new LoginServerDTO();
        if (accountRepository.findByUsername(loginClientDTO.getUsername()) == null){
            loginServerDTO.setUserNameExist(false);

        } else {
            loginServerDTO.setUserNameExist(true);
            if (accountRepository.findByUsernameAndPassword(loginClientDTO.getUsername(), loginClientDTO.getPassword()) == null) {
                loginServerDTO.setPasswordIsTrue(false);
            } else {
                loginServerDTO.setPasswordIsTrue(true);
            }
        }

        return loginServerDTO;
    }
   /* public LoginServerDTO loadAccount(LoginClientDTO loginClientDTO) {
        LoginServerDTO loginServerDTO = new LoginServerDTO();
        String username = loginClientDTO.getUsername();
        String password = loginClientDTO.getPassword();

        if (accountRepository.findByUsername(username) == null){
            loginServerDTO.setUserNameExist(false);

        } else {
            loginServerDTO.setUserNameExist(true);
            Account account = accountRepository.findByUsernameAndPassword(username, password);
            if (account == null) {
                loginServerDTO.setPasswordIsTrue(false);
            } else {
                loginServerDTO.setPasswordIsTrue(true);
                loginServerDTO.setRoles(account.getUser().getRole());
                loginServerDTO.setPermissions(account.getUser().getRole().getPermissions());

            }
        }



        return loginServerDTO;
    }*/

    @Override
    public RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO) {
        Users users = new Users(
                registrationClientDTO.getFirstName(),
                registrationClientDTO.getMiddleName(),
                registrationClientDTO.getLastName(),
                registrationClientDTO.getBirthday(),
                registrationClientDTO.getProfile(),
                registrationClientDTO.getAddress(),
                registrationClientDTO.getEmail(),
                registrationClientDTO.getInstitution());
        Account account = new Account(registrationClientDTO.getAccount().getUsername(),
                passwordEncoder.encode(registrationClientDTO.getAccount().getPassword()));

        users.setAccount(account);
        account.setUser(users);
        //BeanUtils.copyProperties(signupDTO, users);
        RegistrationServerDTO registrationServerDTO = new RegistrationServerDTO();
        if (userRepository.findUsersByEmail(registrationClientDTO.getEmail()) != null){
            registrationServerDTO.isEmailExist(true);
        }
        System.out.println(registrationClientDTO.getAccount().getUsername());
        if (accountRepository.findByUsername(registrationClientDTO.getAccount().getUsername()) != null){
            registrationServerDTO.isUsernameExist(true);
        }

        if (registrationClientDTO.getProfile() == null){
            registrationServerDTO.isProfileIsSet(false);
            return registrationServerDTO;
        }
        userRepository.save(users);
        registrationServerDTO.isProfileIsSet(true);
        registrationServerDTO.isRegistred(true);

        return registrationServerDTO;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Account> optionalUser = accountRepository.findByUsername(username);

        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password wrong"));

        UserDetails userDetails = optionalUser.get();
        new AccountStatusUserDetailsChecker().check(userDetails);

        return userDetails;
    }

    @Override
    public Account getVerificationToken(String token) {
        return accountRepository.findByVerificationToken(token);
    }

    @Override
    public void createVerificationToken(RegistrationClientDTO registrationClientDTO, String token) {
        /*account.setVerificationToken(token);
        accountRepository.save(account);*/
        Account account1 = accountRepository.findByUsername(registrationClientDTO.getAccount().getUsername()).get();
        account1.setVerificationToken(token);
        accountRepository.save(account1);
    }

    @Override
    public void updateAccount(Account account){
        accountRepository.save(account);
    }
}
