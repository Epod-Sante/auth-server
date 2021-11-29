package ca.uqtr.authservice.event.registration_compelte;

import ca.uqtr.authservice.dto.UserRequestDto;
import ca.uqtr.authservice.service.AccountService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.io.File;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
        String templateId = "";
        UserRequestDto user = event.getUser();
        String token = UUID.randomUUID().toString();
        String recipientAddress = user.getEmail().getValue();
        String subject = "I-POD Sante - Confirmation d'inscription";
        String confirmationUrl = REGISTRATION_URL + token;
        service.createRegistrationVerificationToken(user, token);
        String message = "Vous vous êtes inscrit avec succès. activez votre compte: ";

        Mail mail = new Mail();
        mail.setFrom(new Email("sadegh.moulaye.abdallah@uqtr.ca", "I-POD SANTE"));
        mail.setSubject(subject);
        mail.setTemplateId(templateId);
        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("name", user.getFirstName());
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


