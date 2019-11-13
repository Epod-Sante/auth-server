package ca.uqtr.authservice.repository;


import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.entity.vo.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<Users, UUID> {

    @Nullable
    Users findByEmail(Email email);

}
