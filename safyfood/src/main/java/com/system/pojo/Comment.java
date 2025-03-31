package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ???۱
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @ApiModel(value = "Comment对象", description = "???۱")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "comment_id", type = IdType.AUTO)
      private Integer commentId;

    private Integer userId;

    private Integer articleId;

    private Integer foodId;

    private String userName;

    private String commentType;

    private String commentContent;

    private Integer likeCount;

    private String commentStatus;

    private LocalDateTime createTime;

    private String aduitBy;

    private Integer isDeleted;


}
