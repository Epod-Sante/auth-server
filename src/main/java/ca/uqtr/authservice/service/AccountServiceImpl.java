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
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("accountService")
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;


    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LoginServerDTO loadAccount(LoginClientDTO loginClientDTO) {
//        Account account = new Account();
//        BeanUtils.copyProperties(signinDTO, account);

        LoginServerDTO loginServerDTO = new LoginServerDTO();
        if (accountRepository.findByUsername(loginClientDTO.getUsername()) == null){
            loginServerDTO.isUserNameExist(false);

        } else {
            loginServerDTO.isUserNameExist(true);
            if (accountRepository.findByUsernameAndPassword(loginClientDTO.getUsername(), loginClientDTO.getPassword()) == null) {
                loginServerDTO.isPasswordIsTrue(false);
            } else {
                loginServerDTO.isPasswordIsTrue(true);
            }
        }

        return loginServerDTO;
    }

    @Override
    public RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO) {
        Users users = new Users(
                registrationClientDTO.getFirstName(),
                registrationClientDTO.getMidlleName(),
                registrationClientDTO.getLastName(),
                registrationClientDTO.getBirthday(),
                registrationClientDTO.getProfile(),
                registrationClientDTO.getAddress(),
                registrationClientDTO.getEmail(),
                registrationClientDTO.getInstitution());
        Account account = new Account(registrationClientDTO.getAccount().getUsername(),
                registrationClientDTO.getAccount().getPassword(),
                registrationClientDTO.getAccount().isEnabled());

        users.setAccount(account);
        account.setUser(users);
        //BeanUtils.copyProperties(signupDTO, users);
        RegistrationServerDTO registrationServerDTO = new RegistrationServerDTO();
        if (userRepository.findByEmail(registrationClientDTO.getEmail()) != null){
            registrationServerDTO.isEmailExist(true);
            return registrationServerDTO;
        }

        if (registrationClientDTO.getProfile() == null){
            registrationServerDTO.isProfessionIsSet(false);
            return registrationServerDTO;
        }
        userRepository.save(users);
        registrationServerDTO.isProfessionIsSet(true);
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

}
