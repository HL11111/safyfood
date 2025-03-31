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
 * ??Ŀ?
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @ApiModel(value = "Question对象", description = "??Ŀ?")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "question_id", type = IdType.AUTO)
      private Integer questionId;

    private Integer bankId;

    private Integer questionType;

    private String questionTags;

    private Integer questionLevel;

    private String questionContent;

    private String questionOrigin;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String answerContent;

    private String answerAnalysis;

    private Integer usesTimes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;


}
