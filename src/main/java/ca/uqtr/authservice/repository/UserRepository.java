package ca.uqtr.authservice.repository;


import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.entity.vo.Email;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<Users, UUID> {

    //@Query("SELECT user FROM Users user WHERE user.email.value = :email")
    Boolean existsUsersByEmailValue(String email);

    Boolean existsUsersByInstitution_InstitutionCode(String institutionCode);


    List<Users> getAllByInstitution_InstitutionCode(String institutionCode);
    List<Users> findAllByInstitution_InstitutionCode(String institutionCode);

    Users getUsersByAccount_Username(String username);

}
