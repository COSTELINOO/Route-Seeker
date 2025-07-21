package backend.api.service;
import backend.api.entity.City;
import backend.api.mapper.CityMapper;
import backend.api.repository.CityRepository;
import backend.api.dto.CityDTO;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Service
public class CityService {
    public final CityRepository cityRepository;

public CityService(CityRepository cityRepository)
{
    this.cityRepository=cityRepository;
}


    public List<CityDTO> getAllCities() throws Exception {

        List<CityDTO> cities = cityRepository.findAll().stream()
                .map(CityMapper::convertToDTO)
                .toList();

        for (CityDTO city : cities) {
            String imagePath = city.getImage();
            File file = new File(imagePath);
            if (file.exists()) {
                try (InputStream in = new FileInputStream(file)) {
                    byte[] imageBytes = in.readAllBytes();
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    city.setImage(base64Image);
                } catch (IOException e) {
                    city.setImage(null);
                }
            } else {
                city.setImage(null);
            }
        }
return  cities;
    }

public  CityDTO getCityById(Long id) throws Exception
{
    City city=cityRepository.findId(id).orElseThrow(() ->
            new Exception("City with ID " + id + " not found"));
    String imagePath = city.getImage();
    File file = new File(imagePath);
    if (file.exists()) {
        try (InputStream in = new FileInputStream(file)) {
            byte[] imageBytes = in.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            city.setImage(base64Image);
        } catch (IOException e) {
            city.setImage(null);
        }
    } else {
        city.setImage(null);
    }
    return CityMapper.convertToDTO(city);
}

    public  CityDTO getCityByName(String name) throws Exception
    {
        City city=cityRepository.findName(name).orElseThrow(() ->
                new Exception("City with name " + name + " not found"));
        String imagePath = city.getImage();
        File file = new File(imagePath);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                byte[] imageBytes = in.readAllBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                city.setImage(base64Image);
            } catch (IOException e) {
                city.setImage(null);
            }
        } else {
            city.setImage(null);
        }
        return CityMapper.convertToDTO(city);
    }


}
