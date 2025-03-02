package app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompanyDTO {

    private Long id;
    private String name;

    @JsonProperty("logo_path")  // 👈 Matcher TMDb API's JSON-felt til vores Java-felt
    private String logoPath;

    @JsonProperty("origin_country")  // 👈 Matcher "origin_country" fra JSON
    private String originCountry;
}
