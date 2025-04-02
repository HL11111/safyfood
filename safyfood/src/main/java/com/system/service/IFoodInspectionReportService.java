package com.system.service;

import com.system.pojo.FoodInspectionReport;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * ʳƷ???ⱨ??? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IFoodInspectionReportService extends IService<FoodInspectionReport> {

    boolean isExitFoodId(String foodId);

    void updateFoodReport(FoodInspectionReport report);
}
