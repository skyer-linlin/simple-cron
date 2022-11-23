package com.lin.simplecron.dto;

import com.lin.simplecron.domain.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * com.lin.simplecron.dto
 *
 * @author quanlinlin
 * @date 2022/11/23 06:39
 * @since
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CommentDto extends Comment {
    private String prefix;
    private Integer order;
}
