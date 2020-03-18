package ca.uqtr.authservice.event.registration_compelte;

import ca.uqtr.authservice.dto.UserRequestDto;
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
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Value("${spring.profiles.active}")
    private String mailService;
    private AccountService service;
    private JavaMailSender mailSender;
    @Value("${auth-service.registration.url}")
    private String REGISTRATION_URL;

    @Autowired
    public RegistrationListener(AccountService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        if (mailService.equals("dev"))
            this.confirmRegistrationGmail(event);
        else
            this.confirmRegistrationSendGrid(event);
        /*this.confirmRegistrationSendGrid(event);*/

    }

    @SneakyThrows
    private void confirmRegistrationSendGrid(OnRegistrationCompleteEvent event) {
        UserRequestDto user = event.getUser();
        String token = UUID.randomUUID().toString();
        String recipientAddress = user.getEmail().getValue();
        String subject = "POD iSante - Registration Confirmation!";
        Email from = new Email("app158992707@heroku.com");
        Email to = new Email(recipientAddress);
        /*@Value("${mail.uri}")*/
        //String URI_HEROKU = "https://epod-zuul.herokuapp.com/api/v1/auth-service/registrationConfirm?token=";
        String URI_HEROKU = "http://localhost:4200/registration/confirm?token=";
        String confirmationUrl
                = REGISTRATION_URL + token;
        String message = "You registered successfully. Activate your account: ";
        Content content = new Content("text/plain", message+confirmationUrl);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            service.createRegistrationVerificationToken(user, token);
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            sg.api(request);
            /*Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);*/
        } catch (IOException ex) {
            throw ex;
        }
    }


    private void confirmRegistrationGmail(OnRegistrationCompleteEvent event) {
        UserRequestDto user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createRegistrationVerificationToken(user, token);
        String subject = "Registration Confirmation";
        String URI_GMAIL = "http://localhost:8762/api/v1/auth-service/registration/confirm?token=";
        String confirmationUrl
                = URI_GMAIL + token;
        String message = "You registered successfully. Activate your account: ";

        String recipientAddress = user.getEmail().getValue();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+confirmationUrl);
        mailSender.send(email);

    }

}


