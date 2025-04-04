package com.system.mapper;

import com.system.pojo.FoodInspectionReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * ʳƷ???ⱨ??? Mapper 接口
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface FoodInspectionReportMapper extends BaseMapper<FoodInspectionReport> {
    FoodInspectionReport selectReportById(String reportId);
}
