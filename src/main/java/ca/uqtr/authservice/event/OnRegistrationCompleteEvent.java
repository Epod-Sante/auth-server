package ca.uqtr.authservice.event;

import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private RegistrationClientDTO user;

    public OnRegistrationCompleteEvent(
            RegistrationClientDTO user, String appUrl) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
    }

}
