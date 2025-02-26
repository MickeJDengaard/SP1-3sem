package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductionCompanyDTO {


    private Long id;
    private String name;
    private String logoPath;
    private String originCountry;
}
