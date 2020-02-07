package ca.uqtr.authservice.service;

import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.dto.model.EmailDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.UserRepository;
import ca.uqtr.authservice.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    final
    ModelMapper modelMapper;

    public UserServiceImpl(AccountRepository accountRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UserRequestDto> usersList(String adminUsername) {
        System.out.println(adminUsername);
        Account account = accountRepository.getAccountByUsername(adminUsername);
        String institutionCode;
        if (account != null){
            institutionCode = account.getUser().getInstitution().getInstitutionCode();
        }
        else return null;
        List<Users> users = userRepository.findAllByInstitution_InstitutionCode(institutionCode);
        users.removeIf(obj -> obj.getId() == account.getUser().getId());
         return users.stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
    }

    private UserRequestDto convertToDto(Users users) {
        return modelMapper.map(users, UserRequestDto.class);
    }

    @Override
    public void enableUser(String userRequestDto, boolean enable) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserRequestDto user = mapper.readValue(userRequestDto, UserRequestDto.class);
        Account account = accountRepository.getAccountById(UUID.fromString(user.getId()));
        account.isEnabled(enable);
        accountRepository.save(account);
    }
}
