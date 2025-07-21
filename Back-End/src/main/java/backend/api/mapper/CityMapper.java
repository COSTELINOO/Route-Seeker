package backend.api.mapper;

import backend.api.dto.CityDTO;
import backend.api.entity.City;

public class CityMapper {
    public static CityDTO convertToDTO(City city) {
        CityDTO dto = new CityDTO();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setCod(city.getCod());
        dto.setExist(city.getExist());
        dto.setRandom(city.getRandom());
        dto.setImage(city.getImage());
        dto.setPozX(city.getPozX());
        dto.setPozY(city.getPozY());
        return dto;

    }
}
