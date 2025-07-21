package backend.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationDTO
{
    private Long id;
    @NotNull
    private Long idCity;
    @NotNull
   private String name;
    @NotNull
   private Double pozX;
    @NotNull
   private Double pozY;

    private Boolean st=false;
    private Boolean fi=false;
}
