package dtos;

import jakarta.validation.constraints.NotNull;

public class CityDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String cod;
    private Boolean exist = Boolean.FALSE;
    private Boolean random = Boolean.FALSE;
    @NotNull
    private String image;
    @NotNull
    private Double pozX;

    @NotNull
    private Double pozY;

    public CityDTO() {}

    public CityDTO(Long id, String name, String cod, String image,Boolean exist, Boolean random, Double pozX, Double pozY) {
        this.id = id;
        this.name = name;
        this.cod = cod;
        this.exist = exist;
        this.random = random;
        this.pozX = pozX;
        this.pozY = pozY;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCod() { return cod; }
    public void setCod(String cod) { this.cod = cod; }

    public Boolean getExist() { return exist; }
    public void setExist(Boolean exist) { this.exist = exist; }

    public Boolean getRandom() { return random; }
    public void setRandom(Boolean random) { this.random = random; }

    public Double getPozX() { return pozX; }
    public void setPozX(Double pozX) { this.pozX = pozX; }

    public Double getPozY() { return pozY; }
    public void setPozY(Double pozY) { this.pozY = pozY; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    @Override
    public String toString() {
        return this.name;
    }


}