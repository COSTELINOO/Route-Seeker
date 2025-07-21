package dtos;

import jakarta.validation.constraints.NotNull;

public class LocationDTO {
    private Long id;
    @NotNull
    private Long idCity;
    @NotNull
    private String name;
    @NotNull
    private Double pozX;
    @NotNull
    private Double pozY;
    private Boolean st ;
    private Boolean fi ;

    public LocationDTO() {}

    public LocationDTO(Long id, Long idCity, String name, Double pozX, Double pozY, Boolean start, Boolean end) {
        this.id = id;
        this.idCity = idCity;
        this.name = name;
        this.pozX = pozX;
        this.pozY = pozY;
        this.st = start;
        this.fi = end;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCity() {
        return idCity;
    }
    public void setIdCity(Long idCity) {
        this.idCity = idCity;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getPozX() {
        return pozX;
    }
    public void setPozX(Double pozX) {
        this.pozX = pozX;
    }

    public Double getPozY() {
        return pozY;
    }
    public void setPozY(Double pozY) {
        this.pozY = pozY;
    }

    public Boolean getStart() {
        return st;
    }
    public void setStart(Boolean start) {
        this.st = start;
    }

    public Boolean getEnd() {
        return fi;
    }
    public void setEnd(Boolean end) {
        this.fi = end;
    }
    @Override
    public String toString() {
        return this.name;
    }
}