package ca.uqtr.authservice.controller;


import ca.uqtr.authservice.dto.AppointmentDto;
import ca.uqtr.authservice.dto.Request;
import ca.uqtr.authservice.dto.Response;
import ca.uqtr.authservice.service.AppointmentService;
import ca.uqtr.authservice.service.UserService;
import ca.uqtr.authservice.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AppointmentController {

    private ObjectMapper mapper;
    private AppointmentService appointmentService;

    public AppointmentController(ObjectMapper mapper, AppointmentService appointmentService) {
        this.mapper = mapper;
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = "/appointment")
    @ResponseBody
    public Response addAppointment(@RequestBody Request request){
        AppointmentDto appointmentDto = mapper.convertValue(request.getObject(), AppointmentDto.class);
        return appointmentService.addAppointment(appointmentDto);
    }

    @GetMapping(value = "/appointment/all")
    @ResponseBody
    public Response getAppointments(HttpServletRequest HttpRequest){
        String token = HttpRequest.getHeader("Authorization").replace("bearer ","");
        return appointmentService.getAppointments(JwtTokenUtil.getId(token));
    }

    @PutMapping(value = "/appointment")
    @ResponseBody
    public Response updateAppointment(@RequestBody Request request){
        AppointmentDto appointmentDto = mapper.convertValue(request.getObject(), AppointmentDto.class);
        return appointmentService.addAppointment(appointmentDto);
    }

    @DeleteMapping(value = "/appointment")
    @ResponseBody
    public Response deleteAppointment(@RequestBody Request request){
        AppointmentDto appointmentDto = mapper.convertValue(request.getObject(), AppointmentDto.class);
        return appointmentService.addAppointment(appointmentDto);
    }
}
