package ca.uqtr.authservice.repository;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Integer> {
}
