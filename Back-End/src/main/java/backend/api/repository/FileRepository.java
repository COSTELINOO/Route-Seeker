package backend.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.sql.*;

@Repository
public class FileRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public FileRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public  void exportViewToJson( String viewName, String outputFilePath) throws Exception {
        try (Connection conn = this.jdbcTemplate.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + viewName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();


            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonArray = objectMapper.createArrayNode();


            while (rs.next()) {
                ObjectNode jsonRow = objectMapper.createObjectNode();


                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);

                    if (value == null) {
                        jsonRow.putNull(columnName);
                    } else if (value instanceof String) {
                        jsonRow.put(columnName, (String) value);
                    } else if (value instanceof Integer) {
                        jsonRow.put(columnName, (Integer) value);
                    } else if (value instanceof Long) {
                        jsonRow.put(columnName, (Long) value);
                    } else if (value instanceof Double) {
                        jsonRow.put(columnName, (Double) value);
                    } else if (value instanceof Boolean) {
                        jsonRow.put(columnName, (Boolean) value);
                    } else if (value instanceof Date) {
                        jsonRow.put(columnName, value.toString());
                    } else if (value instanceof Timestamp) {
                        jsonRow.put(columnName, value.toString());
                    } else {
                        jsonRow.put(columnName, value.toString());
                    }
                }

                jsonArray.add(jsonRow);
            }


            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(outputFilePath), jsonArray);

            System.out.println("Date exportate cu succes în: " + outputFilePath);

        } catch (Exception e) {
               throw new Exception(e.getMessage());
        }
    }
}
