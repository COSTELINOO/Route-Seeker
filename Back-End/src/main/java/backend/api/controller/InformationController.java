package backend.api.controller;
import backend.api.dto.InformationDTO;
import backend.api.exceptions.MyExeption;
import backend.api.repository.InformationRepository;
import backend.api.service.InformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/informations")
public class InformationController {
    private final InformationService informationService;
    private final InformationRepository informationRepository;

    public InformationController(InformationService informationService, InformationRepository informationRepository) {
        this.informationService = informationService;
        this.informationRepository = informationRepository;
    }
    @GetMapping
    public ResponseEntity<List<InformationDTO>> getAll() throws Exception {

        if(informationRepository.findAll().isEmpty()) {
            throw new MyExeption.NotFound("Nu s-au gasit informatii!");
        }

        List<InformationDTO> informations = informationService.getAllInformations();
        return ResponseEntity.ok(informations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInformationById(
            @PathVariable Long id) throws Exception {

        if(informationRepository.findById(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit informatia cu id-ul"+ " !");
        }
            InformationDTO information = informationService.getInformationByID(id);
            return ResponseEntity.ok(information);

    }

    @PostMapping
    public ResponseEntity<?> createInformation(
            @RequestBody @Valid InformationDTO informationDTO) throws Exception {

        if( informationDTO.getIdCity()==null&&informationDTO.getIdLocation()==null) {
            throw new MyExeption.BadCredentials("Informatia trebuie sa fie neaparat despre un oras sau o locatie!");
        }

        InformationDTO created = informationService.createInformation(informationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInformation(@PathVariable Long id,
                                            @RequestBody @Valid InformationDTO informationDTO) throws Exception {


        if( informationRepository.findById(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit informatia cu id-ul"+ " !");
        }
        if( informationDTO.getIdCity()==null&&informationDTO.getIdLocation()==null) {
            throw new MyExeption.BadCredentials("Informatia trebuie sa fie neaparat despre un oras sau o locatie!");
        }
            InformationDTO updated = informationService.updateInformation(informationDTO, id);
            return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInformation(@PathVariable Long id) throws Exception {
        if( informationRepository.findById(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit informatia cu id-ul"+ " !");
        }

            informationService.deleteInformation(id);
            return ResponseEntity.noContent().build();

    }

}
