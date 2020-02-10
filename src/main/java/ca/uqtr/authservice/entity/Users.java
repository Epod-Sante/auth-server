package ca.uqtr.authservice.entity;

import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@ToString
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class Users extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Temporal(value=TemporalType.DATE)
    @Column(name = "birthday")
    private Date birthday;
    /*@Enumerated(EnumType.STRING)
    @Column(name = "profile")
    private Profile profile;*/
    @Embedded
    private Address address;
    @Embedded
    private Email email;
    @Embedded
    private Institution institution;
    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Account account;


    @ManyToOne
    private Role role;

    public Users(String firstName, String middleName, String lastName, Date birthday, Address address, Email email, Institution institution) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.address = address;
        this.email = email;
        this.institution = institution;
    }

    public Users(Email email, Role role, Institution institution) {
        this.email = email;
        this.role = role;
        this.institution = institution;
    }

    public Users(String firstName, String middleName, String lastName, Date birthday, Address address) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.address = address;
    }
}
