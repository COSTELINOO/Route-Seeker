package staticData;


import dtos.InformationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Informations
{
    public static List<InformationDTO> list=new ArrayList<>();
    public static Map<String,InformationDTO> cityData=new HashMap<>();
    public static Map<String,InformationDTO> locationData=new HashMap<>();
}
