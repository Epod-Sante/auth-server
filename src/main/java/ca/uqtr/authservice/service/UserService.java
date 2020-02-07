package ca.uqtr.authservice.service;


import ca.uqtr.authservice.entity.Users;

import java.util.List;

public interface UserService {

    List<Users> usersList(String adminUsername);
}
