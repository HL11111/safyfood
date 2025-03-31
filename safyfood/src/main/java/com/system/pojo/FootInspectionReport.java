package com.system.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * ʳƷ???ⱨ???
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Getter
@Setter
  @TableName("foot_inspection_report")
@ApiModel(value = "FootInspectionReport对象", description = "ʳƷ???ⱨ???")
public class FootInspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;

      private String foodId;

    private Integer userId;

    private String governmentId;

    private String inspectionDate;

    private String inspectionAgency;

    private String safetyLevel;

    private Boolean isPassed;

    private String complaintId;

    private Integer isDeleted;


}
