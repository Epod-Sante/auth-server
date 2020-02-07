package ca.uqtr.authservice.service;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.UserRepository;
import ca.uqtr.authservice.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Users> usersList(String adminUsername) {
        System.out.println(adminUsername);
        Account account = accountRepository.getAccountByUsername(adminUsername);
        String institutionCode;
        if (account != null){
            institutionCode = account.getUser().getInstitution().getInstitutionCode();
        }
        else return null;
        return userRepository.findAllByInstitution_InstitutionCode(institutionCode);
    }
}
