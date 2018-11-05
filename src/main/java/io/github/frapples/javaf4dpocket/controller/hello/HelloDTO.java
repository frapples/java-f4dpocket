package io.github.frapples.javaf4dpocket.controller.hello;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */

@Data
public class HelloDTO {

    private Integer id;
    private String name;
    private Double percent;
}
