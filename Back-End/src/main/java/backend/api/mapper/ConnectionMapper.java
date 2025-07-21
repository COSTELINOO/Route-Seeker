package backend.api.mapper;

import backend.api.dto.ConnectionDTO;
import backend.api.entity.Connection;

public class ConnectionMapper {
    public static ConnectionDTO convertToDTO(Connection connection) {
        ConnectionDTO dto = new ConnectionDTO();
        dto.setId(connection.getId());
        dto.setIdInt(connection.getIdInt());
        dto.setIdExt(connection.getIdExt());
        return dto;
    }
    public static Connection convertToEntity(ConnectionDTO dto) {
        Connection entity = new Connection();
        entity.setId(dto.getId());
        entity.setIdInt(dto.getIdInt());
        entity.setIdExt(dto.getIdExt());
        return entity;
    }
}
