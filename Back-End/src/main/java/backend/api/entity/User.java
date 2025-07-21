package backend.api.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

}
