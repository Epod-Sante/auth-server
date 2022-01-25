package ca.uqtr.authservice.event.user_invite;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
import ca.uqtr.authservice.dto.UserInviteDto;
import ca.uqtr.authservice.service.AccountService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
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
    private AccountService service;
    private JavaMailSender mailSender;
    @Value("${auth-service.invite.url}")
    private String INVITE_URL;

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
        String templateId = "d-be2a2bc1ce0048269ab8837ab462ed61";
        UserInviteDto invite = event.getUserInviteDto();
        String token = UUID.randomUUID().toString();
        service.createUserInviteToken(invite, token);
        String recipientAddress = invite.getEmail();
        String subject = "I-POD Sante - Invitation";
        String confirmationUrl = INVITE_URL + token;
        String message = "Pour vous inscrire, veuillez cliquer ici: ";

        Mail mail = new Mail();
        mail.setFrom(new Email("sadegh.moulaye.abdallah@uqtr.ca", "I-POD SANTE"));
        mail.setSubject(subject);
        mail.setTemplateId(templateId);
        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("link", confirmationUrl);
        personalization.addTo(new Email(recipientAddress));
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        System.out.println(request.getBody());
        Response response = sg.api(request);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
    }


    private void confirmRegistrationGmail(OnUserInviteEvent event) {
        UserInviteDto invite = event.getUserInviteDto();
        String token = UUID.randomUUID().toString();
        service.createUserInviteToken(invite, token);
        String recipientAddress = invite.getEmail();
        String subject = "POD iSante - Invitation.";
        String URI_GMAIL = "http://localhost:8762/api/v1/auth-service/user/invite?token=";
        String confirmationUrl
                = INVITE_URL + token;
        String message = "To register, click here : ";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+confirmationUrl);
        mailSender.send(email);

    }

}


