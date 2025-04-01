package com.system.pojo.Vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

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
@ApiModel(value = "InspectionReport对象", description = "商家检测报告实体")
public class InspectionReportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报告id")
    private String reportId;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "认证日期")
    private String inspectionDate;

    @Schema(description = "认证机构")
    private String inspectionAgency;

    @Schema(description = "安全标准")
    private List<String> safetyStandards;

    @Schema(description = "安全等级")
    private String safetyLevel;

    @Schema(description = "是否通过")
    @JsonProperty("isPassed")//因为反序列化会被解析为passed
    private Boolean isPassed;

    @Schema(description = "政府id")
    private String governmentId;

    // 将 safetyStandards 转换为 JSON 字符串
    public String getSafetyStandardsJson() {
        Gson gson = new Gson();
        return gson.toJson(safetyStandards);
    }

    // 从 JSON 字符串解析 safetyStandards
    public void setSafetyStandardsJson(String safetyStandardsJson) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        this.safetyStandards = gson.fromJson(safetyStandardsJson, listType);
    }



}
