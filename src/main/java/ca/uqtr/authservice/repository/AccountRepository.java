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
    Optional<Account> findByUsername(String username);

    @Nullable
    Account findByVerificationToken(String token);
}
