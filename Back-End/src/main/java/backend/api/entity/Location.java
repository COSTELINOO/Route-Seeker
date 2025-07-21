package backend.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table(name="locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Setter
    @Getter
    @Id
    private Long id;
    @Setter
    @Getter
    @Column
    private Long idCity;
    @Setter
    @Getter
    @Column
    private String name;
    @Setter
    @Getter
    @Column
    private Double pozX;
    @Setter
    @Getter
    @Column
    private Double pozY;
    @Column
    private Boolean st;
    @Column
    private Boolean fi;

    public Boolean getStart() {
        return st;
    }
    public void setStart(Boolean start) {
        this.st = start;
    }
    public Boolean getEnd() {
        return fi;
    }
    public void setEnd(Boolean end) {
        this.fi = end;
    }


}
