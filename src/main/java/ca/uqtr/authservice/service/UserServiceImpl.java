package ca.uqtr.authservice.service;

import ca.uqtr.authservice.dto.AppointmentDto;
import ca.uqtr.authservice.dto.Response;
import ca.uqtr.authservice.dto.Error;
import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Appointment;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.repository.AccountRepository;
import ca.uqtr.authservice.repository.AppointmentRepository;
import ca.uqtr.authservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.bytecode.stackmap.TypeData;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

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

    @Override
    public UserRequestDto getUserInfos(String username) {
        return convertToDto(userRepository.getUsersByAccount_Username(username)) ;
    }


}
