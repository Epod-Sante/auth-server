package ca.uqtr.authservice.entity;

import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class Users extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "midlle_name")
    private String midlleName;
    @Column(name = "last_name")
    private String lastName;
    @Temporal(value=TemporalType.DATE)
    @Column(name = "birthday")
    private Date birthday;
    @Enumerated(EnumType.STRING)
    @Column(name = "profile")
    private Profile profile;
    @Embedded
    private Address address;
    @Column(name = "email")
    private Email email;
    @Embedded
    private Institution institution;
    /*@OneToOne(mappedBy="user", cascade = CascadeType.ALL)
    private Account account;*/
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "accountnonexpired")
    private boolean accountNonExpired;
    @Column(name = "credentialsnonexpired")
    private boolean credentialsNonExpired;
    @Column(name = "accountnonlocked")
    private boolean accountNonLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_user", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    public Users(String firstName, String midlleName, String lastName, Date birthday, Profile profile, Address address, Email email, Institution institution) {
        this.firstName = firstName;
        this.midlleName = midlleName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.profile = profile;
        this.address = address;
        this.email = email;
        this.institution = institution;
    }

    public Users(Users user) {
        this.firstName = user.getFirstName();
        this.midlleName = user.getMidlleName();
        this.lastName = user.getLastName();
        this.birthday = user.getBirthday();
        this.profile = user.getProfile();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.institution = user.getInstitution();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.accountNonExpired = user.isAccountNonExpired();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.roles = user.getRoles();
    }


}
