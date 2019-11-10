package ca.uqtr.authservice.entity.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Email implements Serializable {

    @Column(name = "email")
    private String value;

    public Email() {
    }

    public Email(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
