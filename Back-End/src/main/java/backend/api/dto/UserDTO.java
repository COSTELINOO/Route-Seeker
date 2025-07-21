package backend.api.dto;

import backend.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements UserDetails {

    @Getter
    private Long id;
    private String username;
    private String password;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

@Override
    public String getUsername() {
        return username;
    }

    @Override
  public String getPassword() {
        return password;
    }

}
