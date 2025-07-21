package backend.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CityDTO
{
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String cod;
    private String image;

    private Boolean exist=Boolean.FALSE;
    private Boolean random=Boolean.FALSE;


    @NotNull
    private Double pozX;
    @NotNull
    private Double pozY;

}
