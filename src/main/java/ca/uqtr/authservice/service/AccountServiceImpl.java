package ca.uqtr.authservice.service;


import ca.uqtr.authservice.controller.AccountController;
import ca.uqtr.authservice.dto.*;
import ca.uqtr.authservice.dto.model.AccountDto;
import ca.uqtr.authservice.dto.model.EmailDto;
import ca.uqtr.authservice.dto.model.PermissionDto;
import ca.uqtr.authservice.dto.model.RoleDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Permission;
import ca.uqtr.authservice.entity.Role;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import ca.uqtr.authservice.event.password_recovery.OnPasswordRecoveryEvent;
import ca.uqtr.authservice.event.registration_compelte.OnRegistrationCompleteEvent;
import ca.uqtr.authservice.event.user_invite.OnUserInviteEvent;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.PermissionRepository;
import ca.uqtr.authservice.repository.RoleRepository;
import ca.uqtr.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
public class AccountServiceImpl implements AccountService {

    private Logger logger = Logger.getLogger(AccountController.class.getName());
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationEventPublisher eventPublisher;


    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleRepository roleRepository, PermissionRepository permissionRepository, ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.eventPublisher = eventPublisher;
    }

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
        RegistrationServerDTO registrationServerDTO = new RegistrationServerDTO();
        Address address= modelMapper.map(registrationClientDTO.getAddressDto(), Address.class);
        Email email = modelMapper.map(registrationClientDTO.getEmailDto(), Email.class);
        Institution institution = modelMapper.map(registrationClientDTO.getInstitutionDto(), Institution.class);
        Role role = modelMapper.map(registrationClientDTO.getRoleDto(), Role.class);

        if (!role.getName().equals("")){
            UUID institutionCode = UUID.randomUUID();
            institution.setInstitutionCode(institutionCode.toString());
        }else{
            Boolean isInstitutionExist = userRepository.existsUsersByInstitution_InstitutionCode(institution.getInstitutionCode());
            if (isInstitutionExist)
                registrationServerDTO.isInstitutionExist(true);
            else {
                registrationServerDTO.isInstitutionExist(false);
                return registrationServerDTO;
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Users users = new Users(
                registrationClientDTO.getFirstName(),
                registrationClientDTO.getMiddleName(),
                registrationClientDTO.getLastName(),
                new Date(format.parse(registrationClientDTO.getBirthday()).getTime()),
                address,
                email,
                institution);
        if (!role.getName().equals("")){
            users.setRole(roleRepository.getRoleByName("role_admin"));
        }
        Account account = new Account(registrationClientDTO.getAccountDto().getUsername(),
                passwordEncoder.encode(registrationClientDTO.getAccountDto().getPassword()));

        users.setAccount(account);
        account.setUser(users);

        if (userRepository.existsUsersByEmailValue(modelMapper.map(registrationClientDTO.getEmailDto(), Email.class).getValue())){
            registrationServerDTO.isEmailExist(true);
            return registrationServerDTO;
        }
        if (accountRepository.existsByUsername(registrationClientDTO.getAccountDto().getUsername())){
            registrationServerDTO.isUsernameExist(true);
            return registrationServerDTO;
        }
        userRepository.save(users);
        this.registrationConfirm(registrationClientDTO, registrationServerDTO);
        return registrationServerDTO;
    }

    @Override
    public RegistrationServerDTO createAccount(String registrationClientDTO) throws ParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        RegistrationClientDTO registration = mapper.readValue(registrationClientDTO, RegistrationClientDTO.class);
        RegistrationServerDTO registrationServerDTO = new RegistrationServerDTO();

        if (accountRepository.existsByUsername(registration.getAccountDto().getUsername())){
            registrationServerDTO.isUsernameExist(true);
            return registrationServerDTO;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Address address= modelMapper.map(registration.getAddressDto(), Address.class);
        Account account = accountRepository.findByEmail(registration.getEmailDto().getValue());
        Users user = account.getUser();
        user.setFirstName(registration.getFirstName());
        user.setMiddleName(registration.getMiddleName());
        user.setLastName(registration.getLastName());
        user.setBirthday(new Date(format.parse(registration.getBirthday()).getTime()));
        user.setAddress(address);
        account.setUsername(registration.getAccountDto().getUsername());
        account.setPassword(passwordEncoder.encode(registration.getAccountDto().getPassword()));
        account.isEnabled(true);

        user.setAccount(account);
        account.setUser(user);

        accountRepository.save(account);
        return registrationServerDTO;
    }

    @Override
    public Account getRegistrationVerificationToken(String token) {
        return accountRepository.findByVerificationToken(token);
    }

    @Override
    public void createRegistrationVerificationToken(RegistrationClientDTO registrationClientDTO, String token) {
        Account account = accountRepository.findByUsername(registrationClientDTO.getAccountDto().getUsername()).orElse(null);
        Objects.requireNonNull(account).setVerificationToken(token);
        Objects.requireNonNull(account).setVerificationTokenExpirationDate(new java.sql.Timestamp (Calendar.getInstance().getTime().getTime()));

        accountRepository.save(account);
    }

    @Override
    public void updateRegistrationVerificationToken(String email) {
        Account account = accountRepository.findByEmail(email);
        RegistrationClientDTO registrationClientDTO = new RegistrationClientDTO();
        registrationClientDTO.setEmailDto(new EmailDto(account.getUser().getEmail().getValue()));
        registrationClientDTO.setAccountDto(new AccountDto(account.getUsername()));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registrationClientDTO));
    }

    @Override
    public void registrationConfirm(RegistrationClientDTO registrationClientDTO, RegistrationServerDTO registrationServerDTO) {
        if (!registrationServerDTO.isEmailExist() && !registrationServerDTO.isUsernameExist()){
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registrationClientDTO));
        }
    }

    @Override
    public void updateAccount(Account account){
        accountRepository.save(account);
    }

    @Override
    public void addRoleToUser(RoleDto roleDto) {
        Optional<Users> user = userRepository.findById(UUID.fromString(roleDto.getUser()));
        Optional<Role> role = roleRepository.findById(roleDto.getRoleId());
        Objects.requireNonNull(user.orElse(null)).setRole(role.orElse(null));
        userRepository.save(user.orElse(null));
    }

    @Override
    public void addPermission(PermissionDto permissionDto) {
        permissionRepository.save(new Permission(permissionDto.getName()));
    }

    @Override
    public Iterable<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public PasswordUpdateDto updatePasswordGetURL(String passwordUpdateDto) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PasswordUpdateDto passwordDto = mapper.readValue(passwordUpdateDto, PasswordUpdateDto.class);

        System.out.println(passwordDto);
        Account account = accountRepository.findByEmail(passwordDto.getEmail());
        if (account != null) {
            passwordDto.setEmailExist(true);
            /*if (account.isEnabled()) {
                passwordDto.setAccountEnabled(true);
                eventPublisher.publishEvent(new OnPasswordRecoveryEvent(passwordDto));
            }*/
            eventPublisher.publishEvent(new OnPasswordRecoveryEvent(passwordDto));
            //TODO !enabled
        }
        System.out.println(passwordDto);
        return passwordDto;
    }

    @Override
    public void createUpdatePasswordToken(PasswordUpdateDto passwordUpdateDto, String token) {
        Account account = accountRepository.findByEmail(passwordUpdateDto.getEmail());
        if (account != null){
            account.setResetPasswordToken(token);
            account.setResetPasswordTokenExpirationDate(new java.sql.Timestamp (Calendar.getInstance().getTime().getTime()));
            accountRepository.save(account);
        }
    }

    @Override
    public void createUserInviteToken(UserInviteDto userInviteDto, String token) {
        Account account = accountRepository.findByEmail(userInviteDto.getEmail());
        if (account != null){
            account.setInviteToken(token);
            account.setInviteTokenExpirationDate(new java.sql.Timestamp (Calendar.getInstance().getTime().getTime()));
            accountRepository.save(account);
        }
    }

    @Override
    public Account getUpdatePasswordToken(String token) {
        return accountRepository.findAccountByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(String token, String password){
        Account account = accountRepository.findAccountByResetPasswordToken(token);
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    @Override
    public UserInviteDto userInvite(String userInviteDto) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserInviteDto userInvite = mapper.readValue(userInviteDto, UserInviteDto.class);
        Boolean userExist = userRepository.existsUsersByEmailValue(userInvite.getEmail());
        if (!userExist){
            Account account = new Account();
            Users admin = userRepository.findByAccount_Username(userInvite.getAdminUsername());
            Email email = new Email(userInvite.getEmail());
            Role role = roleRepository.getRoleByName(userInvite.getRole());
            //Role role = mapper.readValue(userInvite.getRole(), Role.class);
            Users user = new Users(email, role, admin.getInstitution());
            account.setUser(user);
            user.setAccount(account);
            userRepository.save(user);
            eventPublisher.publishEvent(new OnUserInviteEvent(userInvite));
            return userInvite;
        }
        userInvite.setEmailExist(true);
        return userInvite;

    }

    @Override
    public Account getUserInviteToken(String token) {
        return accountRepository.findAccountByInviteToken(token);
    }



}
