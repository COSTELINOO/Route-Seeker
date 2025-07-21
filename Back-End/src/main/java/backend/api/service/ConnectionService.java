package backend.api.service;

import backend.api.dto.ConnectionDTO;
import backend.api.entity.Connection;
import backend.api.mapper.ConnectionMapper;
import backend.api.repository.ConnectionRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ConnectionService {
    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public ConnectionDTO createConnection(ConnectionDTO dto) throws Exception {
        Connection connection = ConnectionMapper.convertToEntity(dto);
        Connection saved = connectionRepository.save(connection);
        return ConnectionMapper.convertToDTO(saved);
    }

    public ConnectionDTO getConnectionByID(Long id) throws Exception {
        Connection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new Exception("Connection with ID " + id + " not found"));
        return ConnectionMapper.convertToDTO(connection);

    }
    public List<ConnectionDTO> getAllConnections() throws Exception {
        return connectionRepository.findAll().stream()
                .map(ConnectionMapper::convertToDTO)
                .toList();
    }

    public ConnectionDTO updateConnection(ConnectionDTO dto, Long id) throws Exception {

        Connection updated = ConnectionMapper.convertToEntity(dto);
        updated.setId(id);
        Connection saved = connectionRepository.update(updated);
        return ConnectionMapper.convertToDTO(saved);
    }

    public void deleteConnection(Long id) throws Exception
    {

        connectionRepository.delete(id);
    }



}
