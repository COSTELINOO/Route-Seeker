package dtos;

public class InformationDTO
{
    private Long id;
    private Long idLocation;
    private Long idCity;
    private String description = "No Description";
    private String type = "Unknown";
    private Boolean dangerZone = false;
    private Boolean city = false;

    public InformationDTO() {}

    public InformationDTO(Long id, Long idLocation, Long idCity, String description, String type, Boolean dangerZone, Boolean city) {
        this.id = id;
        this.idLocation = idLocation;
        this.idCity = idCity;
        this.description = description;
        this.type = type;
        this.dangerZone = dangerZone;
        this.city = city;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLocation() {
        return idLocation;
    }
    public void setIdLocation(Long idLocation) {
        this.idLocation = idLocation;
    }

    public Long getIdCity() {
        return idCity;
    }
    public void setIdCity(Long idCity) {
        this.idCity = idCity;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDangerZone() {
        return dangerZone;
    }
    public void setDangerZone(Boolean dangerZone) {
        this.dangerZone = dangerZone;
    }

    public Boolean getCity() {
        return city;
    }
    public void setCity(Boolean city) {
        this.city = city;
    }
}