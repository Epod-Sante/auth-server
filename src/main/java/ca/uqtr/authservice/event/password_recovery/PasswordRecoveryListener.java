package ca.uqtr.authservice.event.password_recovery;

import ca.uqtr.authservice.dto.PasswordUpdateDto;
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

import java.io.IOException;
import java.util.UUID;


@Component
public class PasswordRecoveryListener implements
        ApplicationListener<OnPasswordRecoveryEvent> {

    @Value("${spring.profiles.active}")
    private String mailService;
    private final AccountService service;
    private final JavaMailSender mailSender;
    @Value("${auth-service.password-recovery.url}")
    private String PASSWORD_RECOVERY_URL;

    @Autowired
    public PasswordRecoveryListener(AccountService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnPasswordRecoveryEvent event) {
        if (mailService.equals("dev"))
            this.confirmRegistrationGmail(event);
        else
            this.confirmRegistrationSendGrid(event);

    }

    @SneakyThrows
    private void confirmRegistrationSendGrid(OnPasswordRecoveryEvent event) {
        String templateId = "";
        PasswordUpdateDto pass = event.getPasswordDto();
        String token = UUID.randomUUID().toString();
        String recipientAddress = pass.getEmail();
        String subject = "I-POD Sante - Mettre à jour le mot de passe";
        String confirmationUrl = PASSWORD_RECOVERY_URL + token;
        service.createUpdatePasswordToken(pass, token);
        String message = "Pour mettre à jour votre mot de passe cliquez ici: ";

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

    private void confirmRegistrationGmail(OnPasswordRecoveryEvent event) {
        PasswordUpdateDto pass = event.getPasswordDto();
        String token = UUID.randomUUID().toString();
        service.createUpdatePasswordToken(pass, token);
        String recipientAddress = pass.getEmail();
        String subject = "POD iSante - Update password!";
        String URI_GMAIL = "http://localhost:8762/api/v1/auth-service/update/password?token=";
        String confirmationUrl
                = PASSWORD_RECOVERY_URL + token;
        String message = "To update your password click here : ";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+confirmationUrl);
        mailSender.send(email);

    }

}


