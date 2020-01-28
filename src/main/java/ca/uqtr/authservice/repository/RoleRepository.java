package ca.uqtr.authservice.repository;


import ca.uqtr.authservice.entity.Role;
import ca.uqtr.authservice.entity.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>  {

    Role getRoleByName(String name);
}
