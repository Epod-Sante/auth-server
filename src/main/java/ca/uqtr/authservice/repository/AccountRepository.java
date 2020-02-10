package ca.uqtr.authservice.repository;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {

    @Nullable
    Account findByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String username);

    Boolean existsAccountByUser_Email_Value(String email);

    Optional<Account> findByUsername(String username);

    @Nullable
    Account findByVerificationToken(String token);

    @Query("UPDATE Account account SET account.password = :password WHERE account.user.email = :email")
    Account updatePasswordByEmail(String password, String email);

    @Query("SELECT account FROM Account account WHERE account.user.email.value = :email")
    Account findByEmail(String email);

    Account findAccountByResetPasswordToken(String token);

    Account findAccountByInviteToken(String token);

    Account getAccountByUsername(String username);

    Account getAccountById(UUID id);

}
