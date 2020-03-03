package ca.uqtr.authservice.repository;

import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, UUID> {
}
