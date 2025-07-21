package backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InformationDTO {
    private Long id;
    private Long idLocation;
    private Long idCity;
    private String description="No Description";
    private String type="Unknown";
    private Boolean dangerZone=false;
    private Boolean city=false;

}
