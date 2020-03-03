package ca.uqtr.authservice.dto;


import lombok.Data;

@Data
public class Request {

    private Object object;

    public Request() {
    }

    public Request(Object object) {
        this.object = object;
    }
}
