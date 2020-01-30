package ca.uqtr.authservice.event.password_recovery;

import ca.uqtr.authservice.dto.RegistrationClientDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnPasswordRecoveryEvent extends ApplicationEvent {
    private String appUrl;
    private RegistrationClientDTO user;

    public OnPasswordRecoveryEvent(
            RegistrationClientDTO user, String appUrl) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
    }

}
