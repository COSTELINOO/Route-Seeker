package backend.api.service;

import backend.api.dto.InformationDTO;
import backend.api.entity.Information;
import backend.api.mapper.InformationMapper;
import backend.api.repository.InformationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InformationService {
    private final InformationRepository informationRepository;

    public InformationService(InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    public InformationDTO createInformation(InformationDTO dto) throws Exception {
        Information information = InformationMapper.convertToEntity(dto);
        Information saved = informationRepository.save(information);
        return InformationMapper.convertToDTO(saved);
    }

    public InformationDTO getInformationByID(Long id) throws Exception {
        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new Exception("Information with ID " + id + " not found"));
        return InformationMapper.convertToDTO(information);

    }
    public List<InformationDTO> getAllInformations() throws Exception {
        return informationRepository.findAll().stream()
                .map(InformationMapper::convertToDTO)
                .toList();
    }

    public InformationDTO updateInformation(InformationDTO dto, Long id) throws Exception {

        dto.setId(id);
        Information updated = InformationMapper.convertToEntity(dto);
        Information saved = informationRepository.update(updated);
        return InformationMapper.convertToDTO(saved);
    }

    public void deleteInformation(Long id) throws Exception
    {

        informationRepository.delete(id);
    }



}
