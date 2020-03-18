package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.UserRequestDto;

import java.io.IOException;
import java.util.List;

public interface UserService {

    List<UserRequestDto> usersList(String adminUsername);

    void enableUser(String userRequestDto, boolean enable) throws IOException;

    UserRequestDto getUserInfos(String username);

}
