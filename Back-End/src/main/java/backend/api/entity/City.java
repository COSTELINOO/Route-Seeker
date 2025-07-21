package backend.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Table(name="cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String cod;

    @Column
    private String image;
    @Column
    private Boolean exist;
    @Column

    private Boolean random;
    @Column
    private Double pozX;
    @Column
    private Double pozY;


}
