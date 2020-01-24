package ca.uqtr.authservice.event;

import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.service.AccountService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {


    private final AccountService service;
    private final JavaMailSender mailSender;

    @Autowired
    public RegistrationListener(AccountService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        RegistrationClientDTO user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm?token=" + token;
        String message = "You registered successfully. Activate your account: ";

            String recipientAddress = user.getEmailDto().getValue();

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(recipientAddress);
            email.setSubject(subject);
            email.setText(message + " " + "http://localhost:8085" + confirmationUrl);
            mailSender.send(email);

           /* String SMTP_HOST_NAME = "smtp.sendgrid.net";
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", SMTP_HOST_NAME);
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getDefaultInstance(props, auth);

            // uncomment for debugging infos to stdout
            // mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();
            MimeMessage msg = new MimeMessage(mailSession);
            Multipart multipart = new MimeMultipart("alternative");
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent("<p>"+message + " " + "http://localhost:8085" + confirmationUrl+"</p>", "text/html");
            multipart.addBodyPart(bodyPart);
            msg.setContent(multipart);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setSubject(subject);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();*/




    }

    /*private static class SMTPAuthenticator extends javax.mail.Authenticator {
        String SMTP_AUTH_USER = System.getenv("SENDGRID_USERNAME");
        String SMTP_AUTH_PWD  = System.getenv("SENDGRID_PASSWORD");
        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }*/
}


