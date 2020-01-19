package ca.uqtr.authservice.dto.model;

import ca.uqtr.authservice.entity.vo.Email;
import ca.uqtr.authservice.entity.vo.Institution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.persistence.Column;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionDto {

    private String institutionName;
    private String institutionCode;


    public Institution institution2Dto(ModelMapper modelMapper) {
        return modelMapper.map(this, Institution.class);
    }

}
