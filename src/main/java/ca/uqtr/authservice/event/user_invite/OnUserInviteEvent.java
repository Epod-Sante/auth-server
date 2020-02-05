package ca.uqtr.authservice.event.user_invite;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.entity.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnUserInviteEvent extends ApplicationEvent {
    private UserInviteDto userInviteDto;

    public OnUserInviteEvent(
            UserInviteDto userInviteDto) {
        super(userInviteDto);

        this.userInviteDto = userInviteDto;
    }

}
