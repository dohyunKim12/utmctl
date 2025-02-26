package dto;

import com.google.gson.JsonObject;

public class UdsDto {
    public UdsDto(Integer status, String message, JsonObject data) {
        this.statusCode = status;
        this.message = message;
        this.data = data;
    }
    public UdsDto() {}
    private Integer statusCode;
    private String message;
    private JsonObject data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}