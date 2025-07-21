package backend.api.repository;

import backend.api.entity.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CityRepository {


    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public CityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<City> findName(String name) throws Exception {
        String sql = "SELECT * FROM cities WHERE name = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = new City();
                    city.setId(rs.getLong("id"));
                    city.setName(rs.getString("name"));
                    city.setCod(rs.getString("cod"));
                    city.setImage(rs.getString("image"));
                    city.setExist(rs.getBoolean("exist"));
                    city.setRandom(rs.getBoolean("random"));
                    city.setPozX(rs.getDouble("poz_x"));
                    city.setPozY(rs.getDouble("poz_y"));

                    return Optional.of(city);
                }
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<City> findId(Long id) throws Exception {
        String sql = "SELECT * FROM cities WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = new City();
                    city.setId(rs.getLong("id"));
                    city.setName(rs.getString("name"));
                    city.setCod(rs.getString("cod"));
                    city.setImage(rs.getString("image"));
                    city.setExist(rs.getBoolean("exist"));
                    city.setRandom(rs.getBoolean("random"));
                    city.setPozX(rs.getDouble("poz_x"));
                    city.setPozY(rs.getDouble("poz_y"));

                    return Optional.of(city);
                }
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return Optional.empty();
    }


    public List<City> findAll() throws Exception {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM cities";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                City city = new City();
                city.setId(rs.getLong("id"));
                city.setName(rs.getString("name"));
                city.setCod(rs.getString("cod"));
                city.setImage(rs.getString("image"));
                city.setExist(rs.getBoolean("exist"));
                city.setRandom(rs.getBoolean("random"));
                city.setPozX(rs.getDouble("poz_x"));
                city.setPozY(rs.getDouble("poz_y"));

                cities.add(city);
            }
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
        return cities;
    }


    public void save(City city) throws Exception {
        String sql = "INSERT INTO cities (name, cod, image,exist, random, poz_x, poz_y) VALUES (?, ?, ?, ?, ?, ?,?) RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city.getName());
            stmt.setString(2, city.getCod());
            stmt.setString(3, city.getImage());
            stmt.setBoolean(4, city.getExist());
            stmt.setBoolean(5, city.getRandom());
            stmt.setDouble(6, city.getPozX());
            stmt.setDouble(7, city.getPozY());

            stmt.executeQuery();
        }   catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }


    public void update(City city) throws Exception {
        String sql = "UPDATE cities SET name=?, cod=?,image=?, exist=?, random=?, poz_x=?, poz_y=? WHERE id=? RETURNING *";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city.getName());
            stmt.setString(2, city.getCod());
            stmt.setString(3, city.getImage());
            stmt.setBoolean(4, city.getExist());
            stmt.setBoolean(5, city.getRandom());
            stmt.setDouble(6, city.getPozX());
            stmt.setDouble(7, city.getPozY());
            stmt.setLong(8, city.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }


    public void delete(long id) throws Exception {
        String sql = "DELETE FROM cities WHERE id = ?";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }

}


