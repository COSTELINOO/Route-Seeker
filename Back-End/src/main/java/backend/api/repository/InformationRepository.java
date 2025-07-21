package backend.api.repository;

import backend.api.entity.Information;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InformationRepository {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public InformationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Optional<Information> findById(Long id) throws Exception {
        String sql = "SELECT * FROM informations WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Information information = new Information();
                    information.setId(rs.getLong("id"));
                    information.setIdLocation(rs.getLong("id_location"));
                    information.setIdCity(rs.getLong("id_city"));
                    information.setDescription(rs.getString("description"));
                    information.setType(rs.getString("type"));
                    information.setDangerZone(rs.getBoolean("danger_zone"));
                    information.setCity(rs.getBoolean("city"));
                    return Optional.of(information);
                }
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }
    public List<Information> findAll() throws Exception {
        List<Information> informations = new ArrayList<>();
        String sql = "SELECT * FROM informations";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Information information = new Information();
                information.setId(rs.getLong("id"));
                information.setIdLocation(rs.getLong("id_location"));
                information.setIdCity(rs.getLong("id_city"));
                information.setDescription(rs.getString("description"));
                information.setType(rs.getString("type"));
                information.setDangerZone(rs.getBoolean("danger_zone"));
                information.setCity(rs.getBoolean("city"));
                informations.add(information);
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return informations;
    }


    public Information save(Information information) throws Exception {
        Information saved=null;
        String sql = "INSERT INTO informations (id_location, id_city, description, type, danger_zone, city) VALUES (?, ?, ?, ?, ?, ?) RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (information.getIdLocation() != null) {
                stmt.setLong(1, information.getIdLocation());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            if (information.getIdCity() != null) {
                stmt.setLong(2, information.getIdCity());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            stmt.setString(3, information.getDescription());
            stmt.setString(4, information.getType());
            stmt.setBoolean(5, information.getDangerZone());
            stmt.setBoolean(6, information.getCity());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saved = new Information();
                    saved.setId(rs.getLong("id"));
                    saved.setIdLocation(rs.getLong("id_location"));
                    saved.setIdCity(rs.getLong("id_city"));
                    saved.setDescription(rs.getString("description"));
                    saved.setType(rs.getString("type"));
                    saved.setDangerZone(rs.getBoolean("danger_zone"));
                    saved.setCity(rs.getBoolean("city"));
                }

            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return saved;
    }


    public Information update(Information information) throws Exception {
        Information saved=null;
        String sql = "UPDATE informations SET id_location=?, id_city=?, description=?, type=?, danger_zone=?, city=? WHERE id=? RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (information.getIdLocation() != null) {
                stmt.setLong(1, information.getIdLocation());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            if (information.getIdCity() != null) {
                stmt.setLong(2, information.getIdCity());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            stmt.setString(3, information.getDescription());
            stmt.setString(4, information.getType());
            stmt.setBoolean(5, information.getDangerZone());
            stmt.setBoolean(6, information.getCity());
            stmt.setLong(7, information.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saved = new Information();
                    saved.setId(rs.getLong("id"));
                    saved.setIdLocation(rs.getLong("id_location"));
                    saved.setIdCity(rs.getLong("id_city"));
                    saved.setDescription(rs.getString("description"));
                    saved.setType(rs.getString("type"));
                    saved.setDangerZone(rs.getBoolean("danger_zone"));
                    saved.setCity(rs.getBoolean("city"));
                }

            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return saved;
    }


    public void delete(Long id) throws Exception {
        String sql = "DELETE FROM informations WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }


}
