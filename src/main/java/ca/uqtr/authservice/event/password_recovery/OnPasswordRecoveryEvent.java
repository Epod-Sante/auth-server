package ca.uqtr.authservice.event.password_recovery;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnPasswordRecoveryEvent extends ApplicationEvent {
    private PasswordUpdateDto passwordDto;

    public OnPasswordRecoveryEvent(
            PasswordUpdateDto passwordDto) {
        super(passwordDto);

        this.passwordDto = passwordDto;
    }

}
