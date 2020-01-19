package ca.uqtr.authservice.dto.model;

import ca.uqtr.authservice.entity.vo.Address;
import ca.uqtr.authservice.entity.vo.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
    private String value;

    public Email email2Dto(ModelMapper modelMapper) {
        return modelMapper.map(this, Email.class);
    }

}
