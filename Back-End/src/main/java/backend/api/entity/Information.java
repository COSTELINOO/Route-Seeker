package backend.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Table(name="informations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {
    @Id
    private Long id;

    @Getter
    @Column
    private Long idLocation;
    @Getter
    @Column
    private Long idCity;
    @Getter
    @Column
    private String description;
    @Getter
    @Column
    private String type;
    @Getter
    @Column
    private Boolean dangerZone;
    @Getter
    @Column
    private Boolean city;
    @Column


    public Long getId() {
        return id;
    }


}


