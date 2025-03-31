package com.system.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ?̼Ҽ??ⱨ???
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("inspection_report")
@ApiModel(value = "InspectionReport对象", description = "?̼Ҽ??ⱨ???")
public class InspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;

      private String reportId;

    private Integer userId;

    private String governmentId;

    private String inspectionDate;

    private String inspectionAgency;

    private String safetyStandard;

    private String safetyLevel;

    private Boolean isPassed;

    private Integer isDeleted;


}
