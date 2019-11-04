package io.github.frapples.javaf4dpocket.comm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */

@AllArgsConstructor
@Getter
public enum ErrorCode {

    SUCCESS("SUCCESS", "success"),
    ERROR("ERROR", "error"),
    ARGUMENT_INVALID("ARGUMENT_INVALID", "argument invalid"),
    ;

    String code;
    String message;
}
