package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ???·????
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("article_feedback")
@ApiModel(value = "ArticleFeedback对象", description = "???·????")
public class ArticleFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "feedback_id", type = IdType.AUTO)
      private Integer feedbackId;

    private Integer articleId;

    private Integer userId;

    private Integer feedbackType;

    private String favoriteStatus;

    private String reportContent;

    private String reportStatus;

    private LocalDateTime createTime;

    private Integer isDeleted;


}
