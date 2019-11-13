package ca.uqtr.authservice;


import ca.uqtr.authservice.controller.AccountController;
import ca.uqtr.authservice.dto.LoginClientDTO;
import ca.uqtr.authservice.dto.LoginServerDTO;
import ca.uqtr.authservice.dto.RegistrationClientDTO;
import ca.uqtr.authservice.dto.RegistrationServerDTO;
import ca.uqtr.authservice.entity.Account;
import ca.uqtr.authservice.entity.Profile;
import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerTests {


    @Autowired
    AccountController accountController;

    @Test
    public void loginTest() {
        Logger.getGlobal().info("Start signInTest test");
        ResponseEntity<LoginServerDTO> account = accountController.login(new LoginClientDTO("lacen", "pass"));
        Assert.assertEquals(HttpStatus.OK, account.getStatusCode());
        Assert.assertTrue(account.hasBody());
        Assert.assertNotNull(account.getBody());
        Logger.getGlobal().info(String.valueOf(account.toString()));
        Logger.getGlobal().info("End signInTest test");
    }

    @Test
    public void registrationTest() throws ParseException {
        Logger.getGlobal().info("Start signUpTest test");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date date = sdf.parse("06/24/2017");
        Address address = new Address("jean tallon", 10, "TR", "XYZ 123", "QB");
        Email email = new Email("2l@gmail.com");
        Institution institution = new Institution("healthcare uqtr", "UQTR110223");
        Account account = new Account("lacen1", "pass1", false);

        RegistrationClientDTO signupDTO = new RegistrationClientDTO("zin", "", "lac", date, Profile.ADMIN, address, email, institution, account);
        ResponseEntity<RegistrationServerDTO> user = accountController.registration(signupDTO);
        Assert.assertEquals(HttpStatus.OK, user.getStatusCode());
        Assert.assertTrue(user.hasBody());
        Assert.assertNotNull(user.getBody());
        Logger.getGlobal().info("End signUpTest test");
    }
}
