package io.github.frapples.javaf4dpocket.comm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseDTO {

    String code;
    String message;
    String description;
    Object data;

    public static ResponseDTO ofSuccess() {
        return ofSuccess(null);
    }

    public static ResponseDTO ofSuccess(Object data) {
        return of(ErrorCode.SUCCESS, data);
    }

    public static ResponseDTO of(ErrorCode errorCode) {
        return of(errorCode, null);
    }

    public static ResponseDTO of(ErrorCode errorCode, Object data) {
        return new ResponseDTO(errorCode.getCode(), errorCode.getMessage(), "", data);
    }

    public static ResponseDTO of(ErrcodeException e) {
        return new ResponseDTO(e.getCode(), e.getMessage(), e.getDescription(), null);
    }
}
