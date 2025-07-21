package backend.api.repository;

import backend.api.entity.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ConnectionRepository {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public ConnectionRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Connection> findById(Long id) throws Exception {
            String sql = "SELECT * FROM connections WHERE id = ?";
            try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                    {
                        Connection connection = new Connection();
                        connection.setId(rs.getLong("id"));
                        connection.setIdInt(rs.getLong("id_int"));
                        connection.setIdExt(rs.getLong("id_ext"));
                        return Optional.of(connection);
                    }
                }
            }
            catch (Exception e) {
               throw new Exception(e.getMessage());
        }
            return Optional.empty();
        }




    public List<Connection> findAll() throws Exception {
        List<Connection> connections = new ArrayList<>();
        String sql = "SELECT * FROM connections";
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Connection connection = new Connection();
                connection.setId(rs.getLong("id"));
                connection.setIdInt(rs.getLong("id_int"));
                connection.setIdExt(rs.getLong("id_ext"));
                connections.add(connection);
            }
        }catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return connections;
    }


    public Connection save(Connection connection) throws Exception {
        Connection saved=null;
        String sql = "INSERT INTO connections (id_int, id_ext) VALUES (?, ?) RETURNING *";
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, connection.getIdInt());
            stmt.setLong(2, connection.getIdExt());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saved = new Connection();
                    saved.setId(rs.getLong("id"));
                    saved.setIdInt(rs.getLong("id_int"));
                    saved.setIdExt(rs.getLong("id_ext"));
                }

            }

        }catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return saved;
    }


    public Connection update(Connection connection) throws Exception {
        Connection updated=null;
        String sql = "UPDATE connections SET id_int = ?, id_ext = ? WHERE id = ? RETURNING *";
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, connection.getIdInt());
            stmt.setLong(2, connection.getIdExt());
            stmt.setLong(3, connection.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    updated = new Connection();
                    updated.setId(rs.getLong("id"));
                    updated.setIdInt(rs.getLong("id_int"));
                    updated.setIdExt(rs.getLong("id_ext"));
                }

            }
        }catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return updated;
    }


    public void delete(Long id) throws Exception {
        String sql = "DELETE FROM connections WHERE id = ?";
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }

}

