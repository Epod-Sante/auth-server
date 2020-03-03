package ca.uqtr.authservice.service;

import ca.uqtr.authservice.dto.AppointmentDto;
import ca.uqtr.authservice.dto.Error;
import ca.uqtr.authservice.dto.Response;
import ca.uqtr.authservice.entity.Appointment;
import ca.uqtr.authservice.entity.Users;
import ca.uqtr.authservice.repository.AppointmentRepository;
import ca.uqtr.authservice.repository.UserRepository;
import javassist.bytecode.stackmap.TypeData;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    private static final Logger LOGGER = Logger.getLogger( TypeData.ClassName.class.getName() );

    private AppointmentRepository appointmentRepository;
    private MessageSource messageSource;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, MessageSource messageSource, UserRepository userRepository, ModelMapper modelMapper) {
        this.appointmentRepository = appointmentRepository;
        this.messageSource = messageSource;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Response addAppointment(AppointmentDto appointmentDto){
        Optional<Users> user = userRepository.findById(appointmentDto.getProfessionalId());
        if (!user.isPresent())
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.user.id", null, Locale.US)),
                            messageSource.getMessage("error.user.message", null, Locale.US)));
        try{
            Type appointmentDtoList = new TypeToken<List<AppointmentDto>>() {}.getType();
            List<Appointment> appointments = user.get().getAppointments();
            appointments.add(appointmentDto.dtoToObj(modelMapper));
            return new Response(modelMapper.map(appointments, appointmentDtoList), null);
        } catch (Exception ex){
            LOGGER.log( Level.ALL, ex.getMessage());
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.null.id", null, Locale.US)),
                            messageSource.getMessage("error.null.message", null, Locale.US)));
        }

    }

    @Override
    public Response getAppointments(String professionalId) {
        Optional<Users> user = userRepository.findById(UUID.fromString(professionalId));
        if (!user.isPresent())
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.user.id", null, Locale.US)),
                            messageSource.getMessage("error.user.message", null, Locale.US)));
        try{
            Type appointmentDtoList = new TypeToken<List<AppointmentDto>>() {}.getType();
            List<Appointment> appointments = user.get().getAppointments();
            return new Response(modelMapper.map(appointments, appointmentDtoList), null);
        } catch (Exception ex){
            LOGGER.log( Level.ALL, ex.getMessage());
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.null.id", null, Locale.US)),
                            messageSource.getMessage("error.null.message", null, Locale.US)));
        }
    }

    @Override
    public Response updateAppointment(AppointmentDto appointmentDto){
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentDto.getProfessionalId());
        if (!appointment.isPresent())
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.appointment.id", null, Locale.US)),
                            messageSource.getMessage("error.appointment.message", null, Locale.US)));
        try{
            appointmentDto.setId(appointment.get().getId().toString());
            return new Response(modelMapper.map(appointmentRepository.save(appointmentDto.dtoToObj(modelMapper)), AppointmentDto.class), null);
        } catch (Exception ex){
            LOGGER.log( Level.ALL, ex.getMessage());
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.null.id", null, Locale.US)),
                            messageSource.getMessage("error.null.message", null, Locale.US)));
        }
    }

    @Override
    public Response deleteAppointment(AppointmentDto appointmentDto){
        try{
            userRepository.deleteById(appointmentDto.getProfessionalId());
            return new Response(appointmentDto, null);
        } catch (Exception ex){
            LOGGER.log( Level.ALL, ex.getMessage());
            return new Response(null,
                    new Error(Integer.parseInt(messageSource.getMessage("error.null.id", null, Locale.US)),
                            messageSource.getMessage("error.null.message", null, Locale.US)));
        }

    }
}
