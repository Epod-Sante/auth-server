package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public LoginServerDTO loadAccount(LoginClientDTO loginClientDTO) {
//        Account account = new Account();
//        BeanUtils.copyProperties(signinDTO, account);

        LoginServerDTO loginServerDTO = new LoginServerDTO();
        Optional<Account> account = accountRepository.findByUsername(loginClientDTO.getUsername());
        if (!account.isPresent()){
            loginServerDTO.setUserNameExist(false);

        } else {
            loginServerDTO.setUserNameExist(true);
            if (account.get().isEnabled()){
                loginServerDTO.setCompteEnabled(true);
                if (accountRepository.findByUsernameAndPassword(loginClientDTO.getUsername(), loginClientDTO.getPassword()) == null) {
                    loginServerDTO.setPasswordIsTrue(false);
                } else {
                    loginServerDTO.setPasswordIsTrue(true);
                }
            }else{
                loginServerDTO.setCompteEnabled(false);
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
    public RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO) throws ParseException {
        Address address= modelMapper.map(registrationClientDTO.getAddressDto(), Address.class);
        Email email = modelMapper.map(registrationClientDTO.getEmailDto(), Email.class);
        Institution institution = modelMapper.map(registrationClientDTO.getInstitutionDto(), Institution.class);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Users users = new Users(
                registrationClientDTO.getFirstName(),
                registrationClientDTO.getMiddleName(),
                registrationClientDTO.getLastName(),
                new Date(format.parse(registrationClientDTO.getBirthday()).getTime()),
                registrationClientDTO.getProfile(),
                address,
                email,
                institution);
        Account account = new Account(registrationClientDTO.getAccountDto().getUsername(),
                passwordEncoder.encode(registrationClientDTO.getAccountDto().getPassword()));

        users.setAccount(account);
        account.setUser(users);

        //BeanUtils.copyProperties(signupDTO, users);
        RegistrationServerDTO registrationServerDTO = new RegistrationServerDTO();
        System.out.println(modelMapper.map(registrationClientDTO.getEmailDto(), Email.class).getValue());
        System.out.println(Objects.requireNonNull(userRepository.findUsersByEmail(modelMapper.map(registrationClientDTO.getEmailDto(), Email.class).getValue())).get().getEmail().getValue());

        if (Objects.requireNonNull(userRepository.findUsersByEmail(modelMapper.map(registrationClientDTO.getEmailDto(), Email.class).getValue())).isPresent()){
            System.out.println("444444444444444444");
            registrationServerDTO.isEmailExist(true);
            System.out.println(registrationServerDTO.toString());
            return registrationServerDTO;
        }
        if (accountRepository.findByUsername(registrationClientDTO.getAccountDto().getUsername()).isPresent()){
            registrationServerDTO.isUsernameExist(true);
            return registrationServerDTO;
        }

        if (registrationClientDTO.getProfile() == null ){
                registrationServerDTO.isProfileIsSet(false);
                return registrationServerDTO;
        }
        userRepository.save(users);
        registrationServerDTO.isProfileIsSet(true);
        registrationServerDTO.isRegistered(true);

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
        Account account1 = accountRepository.findByUsername(registrationClientDTO.getAccountDto().getUsername()).orElse(null);
        Objects.requireNonNull(account1).setVerificationToken(token);
        accountRepository.save(account1);
    }

    @Override
    public void updateAccount(Account account){
        accountRepository.save(account);
    }
}
