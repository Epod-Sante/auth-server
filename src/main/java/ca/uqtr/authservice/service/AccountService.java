package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.*;
import ca.uqtr.authservice.dto.model.PermissionDto;
import ca.uqtr.authservice.dto.model.RoleDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Permission;

import java.io.IOException;
import java.text.ParseException;

public interface AccountService {

    LoginServerDTO loadAccount(LoginClientDTO loginClientDTO);

    RegistrationServerDTO saveAccount(RegistrationClientDTO registrationClientDTO) throws ParseException;

    void createRegistrationVerificationToken(RegistrationClientDTO user, String token);

    void updateRegistrationVerificationToken(String email);

    Account getRegistrationVerificationToken(String VerificationToken);

    void updateAccount(Account account);

    void addRoleToUser(RoleDto roleDto);

    void addPermission(PermissionDto permissionDto);

    void registrationConfirm(RegistrationClientDTO registrationClientDTO, RegistrationServerDTO registrationServerDTO);

    Iterable<Permission> getAllPermissions();

    PasswordUpdateDto updatePasswordGetURL(String passwordUpdateDto) throws IOException;

    void createUpdatePasswordToken(PasswordUpdateDto passwordUpdateDto, String token);

    Account getUpdatePasswordToken(String token);

}
