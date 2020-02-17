package ca.uqtr.authservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

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
    @Column(name = "reset_password_token")
    private String resetPasswordToken ;
    @Column(name = "reset_password_token_expiration_date")
    private Timestamp  resetPasswordTokenExpirationDate ;
    @Column(name = "invite_token")
    private String inviteToken ;
    @Column(name = "invite_token_expiration_date")
    private Timestamp  inviteTokenExpirationDate ;
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        //this.verificationTokenExpirationDate = new java.sql.Timestamp (Calendar.getInstance().getTime().getTime());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        /*this.getUser().getRoles().forEach(role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(permission -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });*/
        grantedAuthorities.add(new SimpleGrantedAuthority(this.getUser().getRole().getName()));
        this.getUser().getRole()
            .getPermissions().forEach(permission -> {
            System.out.println(permission);
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
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

    public void isEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private Date calculateExpirationDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Timestamp getVerificationTokenExpirationDate() {
        return verificationTokenExpirationDate;
    }

    public void setVerificationTokenExpirationDate(Timestamp verificationTokenExpirationDate) {
        this.verificationTokenExpirationDate = verificationTokenExpirationDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getResetPasswordTokenExpirationDate() {
        return resetPasswordTokenExpirationDate;
    }

    public void setResetPasswordTokenExpirationDate(Timestamp resetPasswordTokenExpirationDate) {
        this.resetPasswordTokenExpirationDate = resetPasswordTokenExpirationDate;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public Timestamp getInviteTokenExpirationDate() {
        return inviteTokenExpirationDate;
    }

    public void setInviteTokenExpirationDate(Timestamp inviteTokenExpirationDate) {
        this.inviteTokenExpirationDate = inviteTokenExpirationDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
