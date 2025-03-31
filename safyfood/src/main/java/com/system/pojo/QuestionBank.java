package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ?????
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("question_bank")
@ApiModel(value = "QuestionBank对象", description = "?????")
public class QuestionBank implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "bank_id", type = IdType.AUTO)
      private Integer bankId;

    private Integer userId;

    private String bankType;

    private BigDecimal bankScore;

    private Integer questionCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;


}
