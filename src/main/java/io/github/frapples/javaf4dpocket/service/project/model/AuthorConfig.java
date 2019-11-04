package io.github.frapples.javaf4dpocket.service.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorConfig {

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;
}
