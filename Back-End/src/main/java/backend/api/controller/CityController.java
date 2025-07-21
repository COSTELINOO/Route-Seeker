package backend.api.controller;

import backend.api.dto.CityDTO;
import backend.api.exceptions.MyExeption;
import backend.api.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
public ResponseEntity<List<CityDTO>> getAll() throws Exception {

            List<CityDTO> cities = cityService.getAllCities();
            if (cities == null) {
                throw new MyExeption.NotFound("Nu s-au gasit orase!");
            }
            return ResponseEntity.ok(cities);

    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCityById(
           @PathVariable Long id) throws Exception {


            CityDTO city = cityService.getCityById(id);
            if (city == null) {
                throw new MyExeption.NotFound("Nu s-a gasit orasul cu id-ul"+id+"!");
            }
            return ResponseEntity.ok(city);


    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCityByName(
            @PathVariable String name) throws Exception {

            CityDTO city = cityService.getCityByName(name);
            if (city == null) {
                throw new MyExeption.NotFound("Nu s-a gasit orasul cu numele"+name+"!");
            }
            return ResponseEntity.ok(city);

    }

}
