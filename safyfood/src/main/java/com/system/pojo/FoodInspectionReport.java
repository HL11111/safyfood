package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

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
public class FoodInspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "食品id")
    @TableId(type = IdType.ASSIGN_UUID)
      private String foodId;

   @Schema(description = "商家id")
    private Integer userId;

    @Schema(description = "政府id")
    private String governmentId;

    @Schema(description = "检测事件")
    private String inspectionDate;

    @Schema(description = "检测机构")
    private String inspectionAgency;

    @Schema(description = "安全等级")
    private String safetyLevel;

    @Schema(description = "是否通过")
    @TableField(value = "is_passed", jdbcType = JdbcType.INTEGER)
    private Boolean isPassed;

    @Schema(description = "举报用户id")
    private String complaintId;

    @Schema(description = "是否是删除")
    private Integer isDeleted;


}
