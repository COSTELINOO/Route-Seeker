package backend.api.repository;
import backend.api.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.sql.*;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


public void save(User users) throws Exception {
    String sql = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id, username, password";
    try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql))
    {
        stmt.setString(1, users.getUsername());
        stmt.setString(2, users.getPassword());
        stmt.executeQuery();
    }
   catch (SQLException e) {
               throw new Exception(e.getMessage());
        }
}

public Optional<User> findByUsername(String username) throws Exception {
        String sql = "SELECT  username , password  FROM  users  WHERE username =  ? ";
        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();

                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }
    }