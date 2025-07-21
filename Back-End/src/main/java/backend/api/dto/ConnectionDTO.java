package backend.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConnectionDTO {
    private Long id;
    @NotNull
    private Long idInt;
    @NotNull
    private Long idExt;
}
