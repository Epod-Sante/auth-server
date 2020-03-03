package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.AppointmentDto;
import ca.uqtr.authservice.dto.Response;
import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.entity.Users;

import java.io.IOException;
import java.util.List;

public interface UserService {

    List<UserRequestDto> usersList(String adminUsername);

    void enableUser(String userRequestDto, boolean enable) throws IOException;

    UserRequestDto getUserInfos(String username);

}
