package backend.api.controller;

import backend.api.dto.LocationDTO;
import backend.api.exceptions.MyExeption;
import backend.api.repository.LocationRepository;
import backend.api.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;
    private final LocationRepository locationRepository;

    public LocationController(LocationService locationService, LocationRepository locationRepository) {
        this.locationService = locationService;
        this.locationRepository = locationRepository;
    }
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAll() throws Exception {

        if(locationRepository.findAll().isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit locatii");
        }
        List<LocationDTO> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getLocationById(
            @PathVariable Long id) throws Exception {
    if (locationRepository.findById(id).isEmpty()) {
         throw new MyExeption.NotFound("Nu s-a gasit locatia cu id-ul"+ id+" !");
    }
            LocationDTO location = locationService.getLocationByID(id);
            return ResponseEntity.ok(location);

    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getLocationByName(
            @PathVariable String name) throws Exception {

        if (locationRepository.findName(name).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit locatia cu numele"+ name+" !");
        }
            LocationDTO location = locationService.getLocationByName(name);
            return ResponseEntity.ok(location);


    }


    @PostMapping
    public ResponseEntity<?> createLocation(
            @RequestBody @Valid LocationDTO locationDTO) throws Exception {

        if(locationDTO.getIdCity()==null||locationDTO.getName()==null||locationDTO.getPozX()==null||locationDTO.getPozY()==null) {
            throw new MyExeption.BadCredentials("Urmatoarele campuri nu pot fi null: idCity, name, pozX , pozY");
        }
        if(locationRepository.findName(locationDTO.getName()).isPresent()) {
            throw new MyExeption.Conflict("Locatia cu numele "+locationDTO.getName()+" exista deja!");
        }


        LocationDTO created = locationService.createLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id,
                                              @RequestBody @Valid LocationDTO locationDTO) throws Exception {

      if(locationRepository.findById(id).isEmpty()) {
          throw new MyExeption.NotFound("Nu s-a gasit locatia cu id-ul"+ id+" !");
      }
        if(locationDTO.getIdCity()==null||locationDTO.getName()==null||locationDTO.getPozX()==null||locationDTO.getPozY()==null) {
            throw new MyExeption.BadCredentials("Urmatoarele campuri nu pot fi null: idCity, name, pozX , pozY");
        }

            LocationDTO updated = locationService.updateLocation(locationDTO, id);
            return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) throws Exception {

        if(locationRepository.findById(id).isEmpty()) {
            throw new MyExeption.NotFound("Nu s-a gasit locatia cu id-ul" + id + " !");
        }
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();

    }

}
