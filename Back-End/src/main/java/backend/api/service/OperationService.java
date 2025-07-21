package backend.api.service;

import backend.api.dto.OperationDTO;
import backend.api.repository.OperationRepository;
import org.springframework.stereotype.Service;


@Service
public class OperationService {
    public final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository=operationRepository;
    }



    public  OperationDTO getShortest(String name) throws Exception
    {
        return operationRepository.shortestPath(name);
    }

    public  OperationDTO getLongest(String name) throws Exception
    {
        return operationRepository.longestPath(name);
    }
    public  OperationDTO getCycle(String name) throws Exception
    {
        return operationRepository.cyclePath(name);
    }

    public String create(Long id) throws Exception {
        operationRepository.createLocations( id);
        operationRepository.createConnections(id);
        operationRepository.createInformations(id);
        return "Date generate cu successs" ;
    }

}
