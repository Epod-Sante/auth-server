package ca.uqtr.authservice.event.registration_compelte;

import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private RegistrationClientDTO user;

    public OnRegistrationCompleteEvent(
            RegistrationClientDTO user) {
        super(user);
        this.user = user;
    }

}
