package com.system.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

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
@Data
@TableName("inspection_report")
@ApiModel(value = "InspectionReport对象", description = "商家检测报告实体")
public class InspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报告id")
    @TableId(type = IdType.ASSIGN_UUID)
    private String reportId;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "政府id")
    private String governmentId;

    @Schema(description = "认证日期")
    private String inspectionDate;

    @Schema(description = "认证机构")
    private String inspectionAgency;

    @Schema(description = "安全标准")
    private String safetyStandard;

    @Schema(description = "安全等级")
    private String safetyLevel;

    @Schema(description = "是否通过")
    @TableField(value = "is_passed", jdbcType = JdbcType.INTEGER)
            //自动将 Boolean 类型转换为数据库的 INT（true→1，false→0）
    Boolean isPassed;

    @Schema(description = "是否删除")
    private Integer isDeleted;




}
