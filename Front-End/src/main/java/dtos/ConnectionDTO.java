package dtos;

import jakarta.validation.constraints.NotNull;

public class ConnectionDTO {
    private Long id;
    @NotNull
    private Long idInt;
    @NotNull
    private Long idExt;


    public ConnectionDTO() {}

    public ConnectionDTO(Long id, Long idInt, Long idExt) {
        this.id = id;
        this.idInt = idInt;
        this.idExt = idExt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdInt() {
        return idInt;
    }
    public void setIdInt(Long idInt) {
        this.idInt = idInt;
    }

    public Long getIdExt() {
        return idExt;
    }
    public void setIdExt(Long idExt) {
        this.idExt = idExt;
    }
}