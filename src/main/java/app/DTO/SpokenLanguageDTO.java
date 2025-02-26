package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpokenLanguageDTO {

    private String countryCode;
    private String name;
    private String englishName;
}
