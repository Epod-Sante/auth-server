package ca.uqtr.authservice.dto;

import ca.uqtr.authservice.entity.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class main {


    public static void main(String[] args) throws JsonProcessingException {
        Account user = new Account("test_username", "test_password");
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(user));
    }
}
