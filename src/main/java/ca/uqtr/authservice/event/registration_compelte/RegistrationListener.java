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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import java.io.File;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Value("${sendgrid.apikey}")
    private String SENDGRID_APIKEY;
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
            this.confirmRegistrationGmailWithHTML(event);
        /*this.confirmRegistrationSendGrid(event);*/

    }

    @SneakyThrows
    private void confirmRegistrationSendGrid(OnRegistrationCompleteEvent event) {
        String templateId = "";
        UserRequestDto user = event.getUser();
        String token = UUID.randomUUID().toString();
        String recipientAddress = user.getEmail().getValue();
        String subject = "POD-iSanté - Confirmation d'inscription";
        String confirmationUrl = REGISTRATION_URL + token;
        service.createRegistrationVerificationToken(user, token);
        String message = "Vous vous êtes inscrit avec succès. activez votre compte: ";

        Mail mail = new Mail();
        mail.setFrom(new Email("sadegh.moulaye.abdallah@uqtr.ca", "POD-iSanté"));
        mail.setSubject(subject);
        mail.setTemplateId(templateId);
        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("name", user.getFirstName());
        personalization.addDynamicTemplateData("link", confirmationUrl);
        personalization.addTo(new Email(recipientAddress));
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(SENDGRID_APIKEY);
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

    private void confirmRegistrationGmailWithHTML(OnRegistrationCompleteEvent event) throws MessagingException, UnsupportedEncodingException {
        UserRequestDto user = event.getUser();
        String token = UUID.randomUUID().toString();
        String recipientAddress = user.getEmail().getValue();
        String subject = "POD-iSanté - Confirmation d'inscription";
        String confirmationUrl = REGISTRATION_URL + token;
        service.createRegistrationVerificationToken(user, token);
        String messageContent = "<!doctype html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <title>Simple Transactional Email</title>\n" +
                "    <style>\n" +
                "      /* -------------------------------------\n" +
                "          GLOBAL RESETS\n" +
                "      ------------------------------------- */\n" +
                "      \n" +
                "      /*All the styling goes here*/\n" +
                "      \n" +
                "      img {\n" +
                "        border: none;\n" +
                "        -ms-interpolation-mode: bicubic;\n" +
                "        max-width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      body {\n" +
                "        background-color: #f6f6f6;\n" +
                "        font-family: sans-serif;\n" +
                "        -webkit-font-smoothing: antialiased;\n" +
                "        font-size: 14px;\n" +
                "        line-height: 1.4;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "        -ms-text-size-adjust: 100%;\n" +
                "        -webkit-text-size-adjust: 100%; \n" +
                "      }\n" +
                "\n" +
                "      table {\n" +
                "        border-collapse: separate;\n" +
                "        mso-table-lspace: 0pt;\n" +
                "        mso-table-rspace: 0pt;\n" +
                "        width: 100%; }\n" +
                "        table td {\n" +
                "          font-family: sans-serif;\n" +
                "          font-size: 14px;\n" +
                "          vertical-align: top; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          BODY & CONTAINER\n" +
                "      ------------------------------------- */\n" +
                "\n" +
                "      .body {\n" +
                "        background-color: #f6f6f6;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      /* Set a max-width, and make it display as block so it will automatically stretch to that width, but will also shrink down on a phone or something */\n" +
                "      .container {\n" +
                "        display: block;\n" +
                "        margin: 0 auto !important;\n" +
                "        /* makes it centered */\n" +
                "        max-width: 580px;\n" +
                "        padding: 10px;\n" +
                "        width: 580px; \n" +
                "      }\n" +
                "\n" +
                "      /* This should also be a block element, so that it will fill 100% of the .container */\n" +
                "      .content {\n" +
                "        box-sizing: border-box;\n" +
                "        display: block;\n" +
                "        margin: 0 auto;\n" +
                "        max-width: 580px;\n" +
                "        padding: 10px; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          HEADER, FOOTER, MAIN\n" +
                "      ------------------------------------- */\n" +
                "      .main {\n" +
                "        background: #ffffff;\n" +
                "        border-radius: 3px;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "\n" +
                "      .wrapper {\n" +
                "        box-sizing: border-box;\n" +
                "        padding: 20px; \n" +
                "      }\n" +
                "\n" +
                "      .content-block {\n" +
                "        padding-bottom: 10px;\n" +
                "        padding-top: 10px;\n" +
                "      }\n" +
                "\n" +
                "      .footer {\n" +
                "        clear: both;\n" +
                "        margin-top: 10px;\n" +
                "        text-align: center;\n" +
                "        width: 100%; \n" +
                "      }\n" +
                "        .footer td,\n" +
                "        .footer p,\n" +
                "        .footer span,\n" +
                "        .footer a {\n" +
                "          color: #999999;\n" +
                "          font-size: 12px;\n" +
                "          text-align: center; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          TYPOGRAPHY\n" +
                "      ------------------------------------- */\n" +
                "      h1,\n" +
                "      h2,\n" +
                "      h3,\n" +
                "      h4 {\n" +
                "        color: #000000;\n" +
                "        font-family: sans-serif;\n" +
                "        font-weight: 400;\n" +
                "        line-height: 1.4;\n" +
                "        margin: 0;\n" +
                "        margin-bottom: 30px; \n" +
                "      }\n" +
                "\n" +
                "      h1 {\n" +
                "        font-size: 35px;\n" +
                "        font-weight: 300;\n" +
                "        text-align: center;\n" +
                "        text-transform: capitalize; \n" +
                "      }\n" +
                "\n" +
                "      p,\n" +
                "      ul,\n" +
                "      ol {\n" +
                "        font-family: sans-serif;\n" +
                "        font-size: 14px;\n" +
                "        font-weight: normal;\n" +
                "        margin: 0;\n" +
                "        margin-bottom: 15px; \n" +
                "      }\n" +
                "        p li,\n" +
                "        ul li,\n" +
                "        ol li {\n" +
                "          list-style-position: inside;\n" +
                "          margin-left: 5px; \n" +
                "      }\n" +
                "\n" +
                "      a {\n" +
                "        color: #3498db;\n" +
                "        text-decoration: underline; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          BUTTONS\n" +
                "      ------------------------------------- */\n" +
                "      .btn {\n" +
                "        box-sizing: border-box;\n" +
                "        width: 100%; }\n" +
                "        .btn > tbody > tr > td {\n" +
                "          padding-bottom: 15px; }\n" +
                "        .btn table {\n" +
                "          width: auto; \n" +
                "      }\n" +
                "        .btn table td {\n" +
                "          background-color: #ffffff;\n" +
                "          border-radius: 5px;\n" +
                "          text-align: center; \n" +
                "      }\n" +
                "        .btn a {\n" +
                "          background-color: #ffffff;\n" +
                "          border: solid 1px #3498db;\n" +
                "          border-radius: 5px;\n" +
                "          box-sizing: border-box;\n" +
                "          color: #3498db;\n" +
                "          cursor: pointer;\n" +
                "          display: inline-block;\n" +
                "          font-size: 14px;\n" +
                "          font-weight: bold;\n" +
                "          margin: 0;\n" +
                "          padding: 12px 25px;\n" +
                "          text-decoration: none;\n" +
                "          text-transform: capitalize; \n" +
                "      }\n" +
                "\n" +
                "      .btn-primary table td {\n" +
                "        background-color: #3498db; \n" +
                "      }\n" +
                "\n" +
                "      .btn-primary a {\n" +
                "        background-color: #3498db;\n" +
                "        border-color: #3498db;\n" +
                "        color: #ffffff; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          OTHER STYLES THAT MIGHT BE USEFUL\n" +
                "      ------------------------------------- */\n" +
                "      .last {\n" +
                "        margin-bottom: 0; \n" +
                "      }\n" +
                "\n" +
                "      .first {\n" +
                "        margin-top: 0; \n" +
                "      }\n" +
                "\n" +
                "      .align-center {\n" +
                "        text-align: center; \n" +
                "      }\n" +
                "\n" +
                "      .align-right {\n" +
                "        text-align: right; \n" +
                "      }\n" +
                "\n" +
                "      .align-left {\n" +
                "        text-align: left; \n" +
                "      }\n" +
                "\n" +
                "      .clear {\n" +
                "        clear: both; \n" +
                "      }\n" +
                "\n" +
                "      .mt0 {\n" +
                "        margin-top: 0; \n" +
                "      }\n" +
                "\n" +
                "      .mb0 {\n" +
                "        margin-bottom: 0; \n" +
                "      }\n" +
                "\n" +
                "      .preheader {\n" +
                "        color: transparent;\n" +
                "        display: none;\n" +
                "        height: 0;\n" +
                "        max-height: 0;\n" +
                "        max-width: 0;\n" +
                "        opacity: 0;\n" +
                "        overflow: hidden;\n" +
                "        mso-hide: all;\n" +
                "        visibility: hidden;\n" +
                "        width: 0; \n" +
                "      }\n" +
                "\n" +
                "      .powered-by a {\n" +
                "        text-decoration: none; \n" +
                "      }\n" +
                "\n" +
                "      hr {\n" +
                "        border: 0;\n" +
                "        border-bottom: 1px solid #f6f6f6;\n" +
                "        margin: 20px 0; \n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          RESPONSIVE AND MOBILE FRIENDLY STYLES\n" +
                "      ------------------------------------- */\n" +
                "      @media only screen and (max-width: 620px) {\n" +
                "        table.body h1 {\n" +
                "          font-size: 28px !important;\n" +
                "          margin-bottom: 10px !important; \n" +
                "        }\n" +
                "        table.body p,\n" +
                "        table.body ul,\n" +
                "        table.body ol,\n" +
                "        table.body td,\n" +
                "        table.body span,\n" +
                "        table.body a {\n" +
                "          font-size: 16px !important; \n" +
                "        }\n" +
                "        table.body .wrapper,\n" +
                "        table.body .article {\n" +
                "          padding: 10px !important; \n" +
                "        }\n" +
                "        table.body .content {\n" +
                "          padding: 0 !important; \n" +
                "        }\n" +
                "        table.body .container {\n" +
                "          padding: 0 !important;\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .main {\n" +
                "          border-left-width: 0 !important;\n" +
                "          border-radius: 0 !important;\n" +
                "          border-right-width: 0 !important; \n" +
                "        }\n" +
                "        table.body .btn table {\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .btn a {\n" +
                "          width: 100% !important; \n" +
                "        }\n" +
                "        table.body .img-responsive {\n" +
                "          height: auto !important;\n" +
                "          max-width: 100% !important;\n" +
                "          width: auto !important; \n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      /* -------------------------------------\n" +
                "          PRESERVE THESE STYLES IN THE HEAD\n" +
                "      ------------------------------------- */\n" +
                "      @media all {\n" +
                "        .ExternalClass {\n" +
                "          width: 100%; \n" +
                "        }\n" +
                "        .ExternalClass,\n" +
                "        .ExternalClass p,\n" +
                "        .ExternalClass span,\n" +
                "        .ExternalClass font,\n" +
                "        .ExternalClass td,\n" +
                "        .ExternalClass div {\n" +
                "          line-height: 100%; \n" +
                "        }\n" +
                "        .apple-link a {\n" +
                "          color: inherit !important;\n" +
                "          font-family: inherit !important;\n" +
                "          font-size: inherit !important;\n" +
                "          font-weight: inherit !important;\n" +
                "          line-height: inherit !important;\n" +
                "          text-decoration: none !important; \n" +
                "        }\n" +
                "        #MessageViewBody a {\n" +
                "          color: inherit;\n" +
                "          text-decoration: none;\n" +
                "          font-size: inherit;\n" +
                "          font-family: inherit;\n" +
                "          font-weight: inherit;\n" +
                "          line-height: inherit;\n" +
                "        }\n" +
                "        .btn-primary table td:hover {\n" +
                "          background-color: #34495e !important; \n" +
                "        }\n" +
                "        .btn-primary a:hover {\n" +
                "          background-color: #34495e !important;\n" +
                "          border-color: #34495e !important; \n" +
                "        } \n" +
                "      }\n" +
                "\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\">\n" +
                "      <tr>\n" +
                "        <td>&nbsp;</td>\n" +
                "        <td class=\"container\">\n" +
                "          <div class=\"content\">\n" +
                "\n" +
                "            <!-- START CENTERED WHITE CONTAINER -->\n" +
                "            <table role=\"presentation\" class=\"main\">\n" +
                "\n" +
                "              <!-- START MAIN CONTENT AREA -->\n" +
                "              <tr><td><div style=\"overflow: hidden;\"><font size=\"-1\"><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%;max-width:620px\" align=\"center\">\n" +
                "<tbody><tr>\n" +
                "<td role=\"modules-container\" style=\"padding: 0px 10px; color: rgb(0, 0, 0); text-align: left; --darkreader-inline-bgcolor:#1d2021; --darkreader-inline-color:#d1cfcc;\" bgcolor=\"#F0F0F0\" width=\"100%\" align=\"left\" data-darkreader-inline-bgcolor=\"\" data-darkreader-inline-color=\"\"><span><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"opacity: 0; color: transparent; height: 0px; width: 0px; display: none !important; --darkreader-inline-color:transparent;\" data-darkreader-inline-color=\"\">\n" +
                "<tbody><tr>\n" +
                "<td role=\"module-content\">\n" +
                "<p></p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" style=\"padding: 30px 0px 10px; --darkreader-inline-bgcolor:#1d2021;\" bgcolor=\"#F0F0F0\" data-darkreader-inline-bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr role=\"module-content\">\n" +
                "<td height=\"100%\" valign=\"top\"><table width=\"300\" style=\"width:300px;border-spacing:0;border-collapse:collapse;margin:0px 0px 0px 0px\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding:0px;margin:0px;border-spacing:0\"><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "</table></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><table width=\"300\" style=\"width:300px;border-spacing:0;border-collapse:collapse;margin:0px 0px 0px 0px\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding:0px;margin:0px;border-spacing:0\"><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding:0px 0px 4px 0px\" role=\"module-content\" bgcolor=\"\">\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 20px 15px 15px; line-height: 26px; text-align: inherit; background-color: rgb(212, 136, 204); --darkreader-inline-bgcolor:#62235c;\" height=\"100%\" valign=\"top\" bgcolor=\"#d488cc\" role=\"module-content\" data-darkreader-inline-bgcolor=\"\"><div><div style=\"font-family:inherit;text-align:center\"><span style=\"font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; color: rgb(147, 42, 137); font-size: 24px; --darkreader-inline-color:#c165b9;\" data-darkreader-inline-color=\"\"><strong></strong></span></div><div></div></div></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table></span><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" style=\"padding: 50px 20px; --darkreader-inline-bgcolor:#235275;\" bgcolor=\"#3172a3\" data-darkreader-inline-bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr role=\"module-content\">\n" +
                "<td height=\"100%\" valign=\"top\"><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><table width=\"460\" style=\"width:460px;border-spacing:0;border-collapse:collapse;margin:0px 50px 0px 50px\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding:0px;margin:0px;border-spacing:0\"><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 40px 0px 30px; line-height: 36px; text-align: inherit; background-color: rgb(116, 188, 217); --darkreader-inline-bgcolor:#1f586f;\" height=\"100%\" valign=\"top\" bgcolor=\"#74bcd9\" role=\"module-content\" data-darkreader-inline-bgcolor=\"\"><div><div style=\"font-family:inherit;text-align:center\"><span style=\"font-size: 46px; color: rgb(255, 255, 255); font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; --darkreader-inline-color:#d1cfcc;\" data-darkreader-inline-color=\"\"><strong>POD-iSanté</strong></span></div><div></div></div></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 50px 30px 30px; line-height: 28px; text-align: inherit; background-color: rgb(255, 255, 255); --darkreader-inline-bgcolor:#161718;\" height=\"100%\" valign=\"top\" bgcolor=\"#ffffff\" role=\"module-content\" data-darkreader-inline-bgcolor=\"\"><div><div style=\"font-family:inherit;text-align:inherit\"><span style=\"font-size: 20px; font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; color: rgb(101, 101, 101); --darkreader-inline-color:#979186;\" data-darkreader-inline-color=\"\">Bonjour,</span></div>\n" +
                "<div style=\"font-family:inherit;text-align:inherit\"><br></div>\n" +
                "\n" +
                "\n" +
                "<div style=\"font-family:inherit;text-align:inherit\"><span style=\"font-size: 20px; font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; color: rgb(101, 101, 101); --darkreader-inline-color:#979186;\" data-darkreader-inline-color=\"\">Vous vous êtes inscrit avec succès. activez votre compte:</span></div><div></div></span></div></div></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><span><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"module\" style=\"table-layout:fixed\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"left\" bgcolor=\"#FFFFFF\" style=\"padding: 0px 0px 0px 30px; background-color: rgb(255, 255, 255); --darkreader-inline-bgcolor:#161718;\" data-darkreader-inline-bgcolor=\"\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align:center\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\" bgcolor=\"#932A89\" style=\"border-radius: 6px; font-size: 16px; text-align: left; background-color: inherit; --darkreader-inline-bgcolor: inherit;\" data-darkreader-inline-bgcolor=\"\">\n" +
                "<a href="+confirmationUrl+" style=\"background-color: rgb(147, 42, 137); border: 0px solid rgb(51, 51, 51); border-radius: 0px; color: rgb(255, 255, 255); display: inline-block; font-size: 16px; font-weight: normal; letter-spacing: 0px; line-height: normal; padding: 15px 25px; text-align: center; text-decoration: none; font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; --darkreader-inline-bgcolor:#6a1f63; --darkreader-inline-border-top:#6f675b; --darkreader-inline-border-right:#6f675b; --darkreader-inline-border-bottom:#6f675b; --darkreader-inline-border-left:#6f675b; --darkreader-inline-color:#d1cfcc;\" target=\"_blank\"  data-darkreader-inline-bgcolor=\"\" data-darkreader-inline-border-top=\"\" data-darkreader-inline-border-right=\"\" data-darkreader-inline-border-bottom=\"\" data-darkreader-inline-border-left=\"\" data-darkreader-inline-color=\"\">Cliquez ici</a>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "</font><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 40px 100px 50px 30px; line-height: 26px; text-align: inherit; background-color: rgb(255, 255, 255); --darkreader-inline-bgcolor:#161718;\" height=\"100%\" valign=\"top\" bgcolor=\"#FFFFFF\" role=\"module-content\" data-darkreader-inline-bgcolor=\"\"><div><div style=\"font-family:inherit;text-align:inherit\"><span style=\"font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; font-size: 16px; color: rgb(101, 101, 101); --darkreader-inline-color:#979186;\" data-darkreader-inline-color=\"\">Merci!</span></div><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\">\n" +
                "<div style=\"font-family:inherit;text-align:inherit\"><span style=\"font-family: &quot;trebuchet ms&quot;, helvetica, sans-serif; font-size: 16px; color: rgb(101, 101, 101); --darkreader-inline-color:#979186;\" data-darkreader-inline-color=\"\">POD-iSanté</span></div><div></div></font></div></td></tr></tbody></table></span></td></tr></tbody></table></td></tr></tbody></table><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" style=\"padding: 0px 20px; --darkreader-inline-bgcolor:#62235c;\" bgcolor=\"#D488CC\" data-darkreader-inline-bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr role=\"module-content\">\n" +
                "<td height=\"100%\" valign=\"top\"><table width=\"440\" style=\"width:440px;border-spacing:0;border-collapse:collapse;margin:0px 60px 0px 60px\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding:0px;margin:0px;border-spacing:0\"><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 30px 0px 10px; line-height: 32px; text-align: inherit; background-color: rgb(212, 136, 204); --darkreader-inline-bgcolor:#62235c;\" height=\"100%\" valign=\"top\" bgcolor=\"#D488CC\" role=\"module-content\" data-darkreader-inline-bgcolor=\"\"><div><div style=\"font-family:inherit;text-align:center\"></div><div></div></div></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table><table role=\"module\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\">\n" +
                "<tbody>\n" +
                "</tbody>\n" +
                "</table></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table></td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table></font></td></tr></tbody></table><font color=\"#888888\" data-darkreader-inline-color=\"\" style=\"--darkreader-inline-color:#8d857a;\"><img alt=\"\" width=\"1\" height=\"1\" border=\"0\" style=\"height:1px!important;width:1px!important;border-width:0!important;margin-top:0!important;margin-bottom:0!important;margin-right:0!important;margin-left:0!important;padding-top:0!important;padding-bottom:0!important;padding-right:0!important;padding-left:0!important\"></font></font></div></td></tr>\n" +
                "            <!-- END MAIN CONTENT AREA -->\n" +
                "            </table>\n" +
                "            <!-- END CENTERED WHITE CONTAINER -->\n" +
                "\n" +
                "            \n" +
                "\n" +
                "          </div>\n" +
                "        </td>\n" +
                "        <td>&nbsp;</td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>";


        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email);
        helper.setFrom("uqtrpodisante@gmail.com", "POD-iSanté");
        helper.setTo(recipientAddress);
        helper.setSubject(subject);
        helper.setText(messageContent, true);
        mailSender.send(email);
    }
}


