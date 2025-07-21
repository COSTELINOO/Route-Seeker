package backend.api.service;
import backend.api.dto.LocationDTO;
import backend.api.entity.Location;
import backend.api.mapper.LocationMapper;
import backend.api.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationDTO createLocation(LocationDTO dto) throws Exception {
        Location location = LocationMapper.convertToEntity(dto);
        Location saved = locationRepository.save(location);
        return LocationMapper.convertToDTO(saved);
    }

    public LocationDTO getLocationByID(Long id) throws Exception {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new Exception("Location with ID " + id + " not found"));
        return LocationMapper.convertToDTO(location);

    }
    public LocationDTO getLocationByName(String name) throws Exception {
        Location location = locationRepository.findName(name)
                .orElseThrow(() -> new Exception("Location with name " + name + " not found"));
        return LocationMapper.convertToDTO(location);

    }
    public List<LocationDTO> getAllLocations() throws Exception {
        return locationRepository.findAll().stream()
                .map(LocationMapper::convertToDTO)
                .toList();
    }

    public LocationDTO updateLocation(LocationDTO dto, Long id) throws Exception {
        dto.setId(id);
        Location updated = LocationMapper.convertToEntity(dto);
        Location saved = locationRepository.update(updated);
        return LocationMapper.convertToDTO(saved);
    }

    public void deleteLocation(Long id) throws Exception
    {

        locationRepository.delete(id);
    }



}
