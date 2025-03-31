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
 * ???±
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @ApiModel(value = "Article对象", description = "???±")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "article_id", type = IdType.AUTO)
      private Integer articleId;

    private Integer userId;

    private String articleTitle;

    private String articleType;

    private String articleContent;

    private String articleAuthor;

    private LocalDateTime publishTime;

    private String cityOrigin;

    private Integer likeCount;

    private Integer dislikeCount;

    private Integer commentCount;

    private String aduitBy;

    private String articleStatus;

    private LocalDateTime aduitTime;

    private String illegalQuestion;

    private String favoriteStatus;

    private Integer isDeleted;


}
