package dtos;

public class ErrorDTO {
    String message;
    String error;
    Long code;

    public Long getCode() {
        return code;
    }
    public void setCode(Long code) {
        this.code = code
        ;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
