package backend.api.controller;

import backend.api.dto.ConnectionDTO;
import backend.api.exceptions.MyExeption;
import backend.api.repository.ConnectionRepository;
import backend.api.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final ConnectionRepository connectionRepository;

    public ConnectionController(ConnectionService connectionService, ConnectionRepository connectionRepository) {
        this.connectionService = connectionService;
        this.connectionRepository = connectionRepository;
    }
    @GetMapping
    public ResponseEntity<List<ConnectionDTO>> getAll() throws Exception {
        List<ConnectionDTO> connections = connectionService.getAllConnections();
        if (connections.isEmpty()) {
            throw new MyExeption.NotFound("Nu s-au gasit conexiuni!");
        }
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConnectionById(
            @PathVariable Long id) throws Exception {

            ConnectionDTO connection = connectionService.getConnectionByID(id);
            if (connection == null) {
                throw new MyExeption.NotFound("Nu s-au gasit gasit conexiunea cu id-ul "+id +"!");
            }
            return ResponseEntity.ok(connection);


    }

    @PostMapping
    public ResponseEntity<?> createConnection(
            @RequestBody @Valid ConnectionDTO connectionDTO) throws Exception {

        if(connectionDTO.getIdInt()==null||connectionDTO.getIdExt()==null) {
            throw new MyExeption.BadCredentials("Extremitatile locatiilor nu pot fi nule");
        }
        ConnectionDTO created = connectionService.createConnection(connectionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConnection(@PathVariable Long id,
 @RequestBody @Valid ConnectionDTO connectionDTO) throws Exception {

        if(connectionRepository.findById(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit conexiunia cu id-ul "+id+"!");
        }
        if(connectionDTO.getIdInt()==null||connectionDTO.getIdExt()==null) {
            throw new MyExeption.BadCredentials("Extremitatile locatiilor nu pot fi nule");
        }
            ConnectionDTO updated = connectionService.updateConnection(connectionDTO, id);
            return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConnection(@PathVariable Long id) throws Exception {

   if(connectionRepository.findById(id).isEmpty()) {
       throw new MyExeption.NotFound("Nu s-a gasit conexiunia cu id-ul "+id+"!");

   }
        connectionService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }

}
