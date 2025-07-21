package backend.api.mapper;

import backend.api.dto.InformationDTO;
import backend.api.entity.Information;

public class InformationMapper {
    public static InformationDTO convertToDTO(Information information) {
        InformationDTO informationDTO = new InformationDTO();
        informationDTO.setId(information.getId());
        informationDTO.setIdCity(information.getIdCity());
        informationDTO.setIdLocation(information.getIdLocation());
        informationDTO.setDescription(information.getDescription());
        informationDTO.setType(information.getType());
        informationDTO.setDangerZone(information.getDangerZone());
        informationDTO.setCity(information.getCity());
        return informationDTO;

    }
    public static Information convertToEntity(InformationDTO informationDTO) {
        Information information = new Information();
        information.setId(informationDTO.getId());
        information.setIdCity(informationDTO.getIdCity());
        information.setIdLocation(informationDTO.getIdLocation());
        information.setDescription(informationDTO.getDescription());
        information.setType(informationDTO.getType());
        information.setDangerZone(informationDTO.getDangerZone());
        information.setCity(informationDTO.getCity());
        return information;
    }
}
