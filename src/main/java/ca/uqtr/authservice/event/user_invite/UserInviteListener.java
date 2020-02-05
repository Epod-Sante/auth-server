package ca.uqtr.authservice.event.user_invite;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.service.AccountService;
import com.sendgrid.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
public class UserInviteListener implements
        ApplicationListener<OnUserInviteEvent> {

    @Value("${spring.profiles.active}")
    private String mailService;
    private final AccountService service;
    private final JavaMailSender mailSender;

    @Autowired
    public UserInviteListener(AccountService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnUserInviteEvent event) {
        if (mailService.equals("dev"))
            this.confirmRegistrationGmail(event);
        else
            this.confirmRegistrationSendGrid(event);

    }

    @SneakyThrows
    private void confirmRegistrationSendGrid(OnUserInviteEvent event) {
        UserInviteDto invite = event.getUserInviteDto();
        String token = UUID.randomUUID().toString();
        service.createUserInviteToken(invite, token);
        String recipientAddress = invite.getEmail();
        String subject = "POD iSante - Invitation.";
        Email from = new Email("app158992707@heroku.com");
        Email to = new Email(recipientAddress);
        /*@Value("${mail.uri}")*/
        //String URI_HEROKU = "https://epod-zuul.herokuapp.com/api/v1/auth-service/user/invite?token=";
        String URI_HEROKU = "http://localhost:4200/user/invite?token=";
        String confirmationUrl
                = URI_HEROKU + token;
        String message = "To register, click here : ";
        Content content = new Content("text/plain", message+confirmationUrl);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            sg.api(request);
        } catch (IOException ex) {
            throw ex;
        }
    }


    private void confirmRegistrationGmail(OnUserInviteEvent event) {
        UserInviteDto invite = event.getUserInviteDto();
        String token = UUID.randomUUID().toString();
        service.createUserInviteToken(invite, token);
        String recipientAddress = invite.getEmail();
        String subject = "POD iSante - Invitation.";
        String URI_GMAIL = "http://localhost:8762/api/v1/auth-service/user/invite?token=";
        String confirmationUrl
                = URI_GMAIL + token;
        String message = "To register, click here : ";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+confirmationUrl);
        mailSender.send(email);

    }

}


