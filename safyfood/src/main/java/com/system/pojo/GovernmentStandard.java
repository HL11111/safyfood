package com.system.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ??????׼?
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("government_standard")
@ApiModel(value = "GovernmentStandard对象", description = "??????׼?")
public class GovernmentStandard implements Serializable {

    private static final long serialVersionUID = 1L;

      private String standardId;

    private String governmentId;

    private String standardName;

    private String description;

    private Integer validityPeriod;

    private Integer isDeleted;


}
