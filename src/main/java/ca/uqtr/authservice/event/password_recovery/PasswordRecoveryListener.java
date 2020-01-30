//package ca.uqtr.authservice.event.password_recovery;
//
//import ca.uqtr.authservice.dto.RegistrationClientDTO;
//import ca.uqtr.authservice.event.registration_compelte.OnRegistrationCompleteEvent;
//import ca.uqtr.authservice.service.AccountService;
//import com.sendgrid.*;
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationListener;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.UUID;
//
//
//@Component
//public class PasswordRecoveryListener implements
//        ApplicationListener<OnRegistrationCompleteEvent> {
//
//    @Value("${spring.profiles.active}")
//    private String mailService;
//    private final AccountService service;
//    private final JavaMailSender mailSender;
//
//    @Autowired
//    public PasswordRecoveryListener(AccountService service, JavaMailSender mailSender) {
//        this.service = service;
//        this.mailSender = mailSender;
//    }
//
//    @SneakyThrows
//    @Override
//    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
//        if (mailService.equals("dev"))
//            this.confirmRegistrationGmail(event);
//        else
//            this.confirmRegistrationSendGrid(event);
//        /*this.confirmRegistrationSendGrid(event);*/
//
//    }
//
//    @SneakyThrows
//    private void confirmRegistrationSendGrid(OnRegistrationCompleteEvent event) {
//        RegistrationClientDTO user = event.getUser();
//        String token = UUID.randomUUID().toString();
//        service.createVerificationToken(user, token);
//        String recipientAddress = user.getEmailDto().getValue();
//        String subject = "POD iSante - Registration Confirmation!";
//        Email from = new Email("app158992707@heroku.com");
//        Email to = new Email(recipientAddress);
//        /*@Value("${mail.uri}")*/
//        String URI_HEROKU = "https://epod-zuul.herokuapp.com/api/v1/auth-server/registrationConfirm?token=";
//        String confirmationUrl
//                = URI_HEROKU + token;
//        String message = "You registered successfully. Activate your account: ";
//        Content content = new Content("text/plain", message+confirmationUrl);
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
//        Request request = new Request();
//        try {
//            request.method = Method.POST;
//            request.endpoint = "mail/send";
//            request.body = mail.build();
//            sg.api(request);
//            /*Response response = sg.api(request);
//            System.out.println(response.statusCode);
//            System.out.println(response.body);
//            System.out.println(response.headers);*/
//        } catch (IOException ex) {
//            throw ex;
//        }
//    }
//
//
//    private void confirmRegistrationGmail(OnRegistrationCompleteEvent event) {
//        RegistrationClientDTO user = event.getUser();
//        String token = UUID.randomUUID().toString();
//        service.createVerificationToken(user, token);
//        String subject = "Registration Confirmation";
//        String URI_GMAIL = "http://localhost:8762/api/v1/auth-service/registrationConfirm?token=";
//        String confirmationUrl
//                = URI_GMAIL + token;
//        String message = "You registered successfully. Activate your account: ";
//
//        String recipientAddress = user.getEmailDto().getValue();
//
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText(message+confirmationUrl);
//        mailSender.send(email);
//
//       /* String SMTP_HOST_NAME = "smtp.sendgrid.net";
//        Properties props = new Properties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.host", SMTP_HOST_NAME);
//        props.put("mail.smtp.port", 587);
//        props.put("mail.smtp.auth", "true");
//        Authenticator auth = new SMTPAuthenticator();
//        Session mailSession = Session.getDefaultInstance(props, auth);
//
//        // uncomment for debugging infos to stdout
//        // mailSession.setDebug(true);
//        Transport transport = mailSession.getTransport();
//        MimeMessage msg = new MimeMessage(mailSession);
//        Multipart multipart = new MimeMultipart("alternative");
//        BodyPart bodyPart = new MimeBodyPart();
//        bodyPart.setContent("<p>"+message + " " + "http://localhost:8085" + confirmationUrl+"</p>", "text/html");
//        multipart.addBodyPart(bodyPart);
//        msg.setContent(multipart);
//        msg.setFrom(new InternetAddress(fromEmail));
//        msg.setSubject(subject);
//        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//
//        transport.connect();
//        transport.sendMessage(message,
//                message.getRecipients(Message.RecipientType.TO));
//        transport.close();*/
//
//    }
//
//    /*private static class SMTPAuthenticator extends javax.mail.Authenticator {
//        String SMTP_AUTH_USER = System.getenv("SENDGRID_USERNAME");
//        String SMTP_AUTH_PWD  = System.getenv("SENDGRID_PASSWORD");
//        public PasswordAuthentication getPasswordAuthentication() {
//            String username = SMTP_AUTH_USER;
//            String password = SMTP_AUTH_PWD;
//            return new PasswordAuthentication(username, password);
//        }
//    }*/
//}
//
//
