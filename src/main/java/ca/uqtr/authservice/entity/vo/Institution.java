package ca.uqtr.authservice.entity.vo;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Institution implements Serializable {

    @Column(name = "institution_name")
    private String institutionName;
    @Column(name = "institution_code")
    private String institutionCode;

}
