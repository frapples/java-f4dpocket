package io.github.frapples.javaf4dpocket.comm.response;

import lombok.Getter;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */

@Getter
public class ErrcodeException extends RuntimeException {

    String code;
    String message;
    String description;

    public ErrcodeException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.description = description;
    }

    public ErrcodeException(ErrorCode errorCode, String description, Throwable e) {
        super(errorCode.getMessage(), e);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.description = description;
    }

    public static ErrcodeException ofArgumentInvalid(String description) {
        return new ErrcodeException(ErrorCode.ARGUMENT_INVALID, description);
    }
}
