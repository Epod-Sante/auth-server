package ca.uqtr.authservice.dto;


import lombok.Data;

@Data
public class Response {

    private Object object;
    private Error error;

    public Response(Object object, Error error) {
        this.object = object;
        this.error = error;
    }
}
