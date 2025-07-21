package backend.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Table(name="connections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
    @Id
    private Long id;
    @Column
    private Long idInt;
    @Column
    private Long idExt;

}
