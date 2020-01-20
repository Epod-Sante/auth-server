package ca.uqtr.authservice.dto.model;

import ca.uqtr.authservice.entity.vo.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private String street;
    private Integer streetNumber;
    private String city;
    private String postalCode;
    private String province;

}
