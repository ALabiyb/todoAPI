package tz.co.softnet.softcms.frontendapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ResponseModel<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private boolean success;


    public ResponseModel(Integer statusCode, String message, T data, boolean success) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> ResponseModel<T> success(Integer statusCode, String message, T data) {
        return new ResponseModel<T>(statusCode, message, data, true);
    }

    public static <T> ResponseModel<T> success(Integer statusCode, String message) {
        return new ResponseModel<T>(statusCode, message, null, true);
    }

    public static <T> ResponseModel<T> error(Integer statusCode, String message) {
        return new ResponseModel<T>(statusCode, message, null, false);
    }
}

