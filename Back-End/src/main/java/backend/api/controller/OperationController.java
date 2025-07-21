package backend.api.controller;

import backend.api.dto.OperationDTO;

import backend.api.exceptions.MyExeption;
import backend.api.repository.CityRepository;
import backend.api.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final OperationService operationService;
    private final CityRepository cityRepository;

    public OperationController(OperationService operationService, CityRepository cityRepository) {
        this.operationService = operationService;
        this.cityRepository = cityRepository;
    }


    @GetMapping("/shortest/{name}")
    public ResponseEntity<?> getShortestPath(@PathVariable String name) throws Exception {

        if(cityRepository.findName(name).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit orasul cu numele "+name+" !");
        }
            OperationDTO operation = operationService.getShortest(name);
            return ResponseEntity.ok(operation);

    }

    @GetMapping("/longest/{name}")
    public ResponseEntity<?> getLongestPath(@PathVariable String name) throws Exception {
        if(cityRepository.findName(name).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit orasul cu numele "+name+" !");
        }
            OperationDTO operation = operationService.getLongest(name);
            return ResponseEntity.ok(operation);

    }

    @GetMapping("/cycle/{name}")
    public ResponseEntity<?> getCyclePath(@PathVariable String name) throws Exception {
        if(cityRepository.findName(name).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit orasul cu numele "+name+" !");
        }
            OperationDTO operation = operationService.getCycle(name);
            return ResponseEntity.ok(operation);

    }

    @PostMapping("/random/{id}")
    public ResponseEntity<?> generateRandomOperation(@PathVariable Long id) throws Exception {

        if(cityRepository.findId(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit orasul cu id-ul "+id+" !");
        }

            String operation = operationService.create(id);
            return ResponseEntity.ok(operation);
    }



}
