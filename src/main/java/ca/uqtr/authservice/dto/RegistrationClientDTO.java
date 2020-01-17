package ca.uqtr.authservice.dto;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Profile;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationClientDTO implements Serializable {

    static final long serialVersionUID = 1L;
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String middleName;
    @NotNull
    @NotEmpty
    private String lastName;
    @NotNull
    @NotEmpty
    private Date birthday;
    @NotNull
    @NotEmpty
    private Profile profile;
    @NotNull
    @NotEmpty
    private Address address;
    @NotNull
    @NotEmpty
    private Email email;
    @NotNull
    @NotEmpty
    private Institution institution;
    @NotNull
    @NotEmpty
    private Account account;
}
