package backend.api.service;

import backend.api.repository.FileRepository;
import org.springframework.stereotype.Service;


@Service
public class FileService {
    public final FileRepository fileRepository;

    public FileService(FileRepository fileRepository ) {
        this.fileRepository=fileRepository;
    }
    public String jsonConnections() throws Exception
    {

        fileRepository.exportViewToJson("v_connections_details","connections.json");
        return "{\"error\":\"No error\", \"message\": \" Fisier conexiuni Creat cu success\", \"code\": 200}" ;

    }
    public String jsonCities() throws Exception
    {

        fileRepository.exportViewToJson("v_cities_details","cities.json");
        return "{\"error\":\"No error\", \"message\": \" Fisier orase Creat cu success\", \"code\": 200}" ;
    }
    public String jsonLocations() throws Exception
    {
        fileRepository.exportViewToJson("v_location_details","locations.json");
        return "{\"error\":\"No error\", \"message\": \" Fisier locatii Creat cu success\", \"code\": 200}" ;
       }

}
