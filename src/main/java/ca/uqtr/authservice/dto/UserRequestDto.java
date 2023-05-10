package ca.uqtr.authservice.dto;

import ca.uqtr.authservice.dto.model.*;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Profile;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto implements Serializable {

    static final long serialVersionUID = 1L;


    private String id;

    private String firstName;

    private String middleName;
    private String lastName;
    private String birthday;
    private AddressDto address;
    private EmailDto email;
    private InstitutionDto institution;
    private AccountDto account;
    private RoleDto role;

    public UserRequestDto(String firstName, String middleName, String lastName, String birthday, AddressDto address, EmailDto email, InstitutionDto institution, AccountDto account, RoleDto role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.address = address;
        this.email = email;
        this.institution = institution;
        this.account = account;
        this.role = role;
    }
}
