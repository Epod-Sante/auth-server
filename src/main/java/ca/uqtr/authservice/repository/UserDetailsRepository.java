package ca.uqtr.authservice.repository;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<Users, UUID> {


    Optional<Users> findByUsername(String username);

}