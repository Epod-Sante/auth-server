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

    UserResponseDto saveAccount(UserRequestDto userRequestDto) throws ParseException;

    void createRegistrationVerificationToken(UserRequestDto user, String token);

    void updateRegistrationVerificationToken(String email);

    Account getRegistrationVerificationToken(String VerificationToken);

    UserResponseDto createAccount(String registrationClientDTO) throws ParseException, IOException;

    void updateAccount(Account account);

    void addRoleToUser(RoleDto roleDto);

    void addPermission(PermissionDto permissionDto);

    void registrationConfirm(UserRequestDto userRequestDto, UserResponseDto userResponseDto);

    Iterable<Permission> getAllPermissions();

    PasswordUpdateDto updatePasswordGetURL(String passwordUpdateDto) throws IOException;

    void createUpdatePasswordToken(PasswordUpdateDto passwordUpdateDto, String token);

    void createUserInviteToken(UserInviteDto userInviteDto, String token);

    Account getUpdatePasswordToken(String token);

    void updatePassword(String email, String password);

    UserInviteDto userInvite(String userInviteDto) throws IOException;

    Account getUserInviteToken(String token);

}
