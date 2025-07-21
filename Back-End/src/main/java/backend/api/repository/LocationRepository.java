package backend.api.repository;
import backend.api.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository

public class LocationRepository {


    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Location> findName(String name) throws Exception {
        String sql = "SELECT * FROM locations WHERE name = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getLong("id"));
                    location.setIdCity(rs.getLong("id_city"));
                    location.setName(rs.getString("name"));
                    location.setPozX(rs.getDouble("poz_x"));
                    location.setPozY(rs.getDouble("poz_y"));
                    location.setStart(rs.getBoolean("st"));
                    location.setEnd(rs.getBoolean("fi"));
                    return Optional.of(location);
                }
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }
    public Optional<Location> findById(long id) throws Exception {
        String sql = "SELECT * FROM locations WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getLong("id"));
                    location.setIdCity(rs.getLong("id_city"));
                    location.setName(rs.getString("name"));
                    location.setPozX(rs.getDouble("poz_x"));
                    location.setPozY(rs.getDouble("poz_y"));
                    location.setStart(rs.getBoolean("st"));
                    location.setEnd(rs.getBoolean("fi"));
                    return Optional.of(location);
                }
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }


    public List<Location> findAll() throws Exception {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Location location = new Location();
                location.setId(rs.getLong("id"));
                location.setIdCity(rs.getLong("id_city"));
                location.setName(rs.getString("name"));
                location.setPozX(rs.getDouble("poz_x"));
                location.setPozY(rs.getDouble("poz_y"));
                location.setStart(rs.getBoolean("st"));
                location.setEnd(rs.getBoolean("fi"));
                locations.add(location);
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return locations;
    }


    public Location save(Location location) throws Exception {
        Location saved=null;
        String sql = "INSERT INTO locations (id_city, name, poz_x, poz_y, st ,fi ) VALUES (?, ?, ?, ?,?,?) RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, location.getIdCity());
            stmt.setString(2, location.getName());
            stmt.setDouble(3, location.getPozX());
            stmt.setDouble(4, location.getPozY());
            stmt.setBoolean(5, location.getStart());
            stmt.setBoolean(6, location.getEnd());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saved = new Location();
                    saved.setId(rs.getLong("id"));
                    saved.setIdCity(rs.getLong("id_city"));
                    saved.setName(rs.getString("name"));
                    saved.setPozX(rs.getDouble("poz_x"));
                    saved.setPozY(rs.getDouble("poz_y"));
                    saved.setStart(rs.getBoolean("st"));
                    saved.setEnd(rs.getBoolean("fi"));
                }

                }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return saved;
    }


    public Location update(Location location) throws Exception {
        Location saved=null;
        String sql = "UPDATE locations SET id_city=?, name=?, poz_x=?, poz_y=?, st=?,fi=? WHERE id=? RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, location.getIdCity());
            stmt.setString(2, location.getName());
            stmt.setDouble(3, location.getPozX());
            stmt.setDouble(4, location.getPozY());
            stmt.setBoolean(5, location.getStart());
            stmt.setBoolean(6, location.getEnd());
            stmt.setLong(7, location.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saved = new Location();
                    saved.setId(rs.getLong("id"));
                    saved.setIdCity(rs.getLong("id_city"));
                    saved.setName(rs.getString("name"));
                    saved.setPozX(rs.getDouble("poz_x"));
                    saved.setPozY(rs.getDouble("poz_y"));
                    saved.setStart(rs.getBoolean("st"));
                    saved.setEnd(rs.getBoolean("fi"));
                }

            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return saved;
    }


    public void delete(long id) throws Exception {
        String sql = "DELETE FROM locations WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }
}

