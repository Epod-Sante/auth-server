package ca.uqtr.authservice.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "account", schema = "public")
public class Account implements UserDetails {
    @Id
    UUID id;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled = false;
    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;
    @Column(name = "verification_token")
    private String verificationToken ;
    @Column(name = "verification_token_expiration_date")
    private Timestamp  verificationTokenExpirationDate ;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.verificationTokenExpirationDate = new java.sql.Timestamp (Calendar.getInstance().getTime().getTime());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        this.getUser().getRoles().forEach(role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(permission -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    private Date calculateExpirationDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

}
