package backend.api.mapper;

import backend.api.dto.LocationDTO;
import backend.api.entity.Location;

public class LocationMapper {
    public static LocationDTO convertToDTO(Location location) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(location.getId());
        locationDTO.setIdCity(location.getIdCity());
        locationDTO.setName(location.getName());
        locationDTO.setPozX(location.getPozX());
        locationDTO.setPozY(location.getPozY());
        locationDTO.setSt(location.getStart());
        locationDTO.setFi(location.getEnd());
        return locationDTO;
    }
    public static Location convertToEntity(LocationDTO locationDTO) {
        Location location = new Location();
        location.setId(locationDTO.getId());
        location.setIdCity(locationDTO.getIdCity());
        location.setName(locationDTO.getName());
        location.setPozX(locationDTO.getPozX());
        location.setPozY(locationDTO.getPozY());
        location.setStart(locationDTO.getSt());
        location.setEnd(locationDTO.getFi());
        return location;
    }

}
