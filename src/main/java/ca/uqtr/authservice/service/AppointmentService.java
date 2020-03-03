package ca.uqtr.authservice.service;


import ca.uqtr.authservice.dto.AppointmentDto;
import ca.uqtr.authservice.dto.Response;

public interface AppointmentService {
    Response addAppointment(AppointmentDto appointmentDto);

    Response getAppointments(String professionalId);

    Response updateAppointment(AppointmentDto appointmentDto);

    Response deleteAppointment(AppointmentDto appointmentDto);

}
