package ca.uqtr.authservice.dto.model;

import ca.uqtr.authservice.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private String username;
    private String password;
    private boolean enabled = false;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private String verificationToken ;
    private Date verificationTokenExpirationDate ;

    public AccountDto(String username) {
        this.username = username;
    }

    public Account account2Dto(ModelMapper modelMapper) {
        return modelMapper.map(this, Account.class);
    }

}
