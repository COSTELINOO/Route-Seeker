package staticData;

import dtos.CityDTO;

import java.util.*;

public class Cities {
    public static Long currentCityId=0L;
    public static  List<CityDTO> list=new ArrayList<>();
    public static  Map<String,CityDTO> data=new HashMap<>();
    public static Map<String,String> descriptions=new HashMap<>();
    public static Map<String,Boolean>existaTraseu=new HashMap<>();
    public static Map<String,Boolean>existaRandom=new HashMap<>();


}