package ca.uqtr.authservice.event.registration_compelte;

import ca.uqtr.authservice.dto.UserRequestDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private UserRequestDto user;

    public OnRegistrationCompleteEvent(
            UserRequestDto user) {
        super(user);
        this.user = user;
    }

}
