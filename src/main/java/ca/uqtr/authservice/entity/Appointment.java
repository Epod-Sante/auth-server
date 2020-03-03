package ca.uqtr.authservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@ToString
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Entity
@Table(name = "appointment", schema = "public")
public class Appointment extends BaseEntity {

    @Column(name = "patient_id")
    private UUID patientId;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "appointment_date")
    private Date appointmentDate;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;
}
