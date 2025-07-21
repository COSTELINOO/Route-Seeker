package backend.api.controller;


import backend.api.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    @PostMapping("/connections")
    public ResponseEntity<?> createConnections() throws Exception {

           String  aux = fileService.jsonConnections();
            return ResponseEntity.ok(aux);

    }

    @PostMapping("/cities")
    public ResponseEntity<?> createCities() throws Exception {
            String  aux = fileService.jsonCities();
            return ResponseEntity.ok(aux);
    }

    @PostMapping("/locations")
    public ResponseEntity<?> createLocations() throws Exception {
            String  aux = fileService.jsonLocations();
            return ResponseEntity.ok(aux);
    }


}
