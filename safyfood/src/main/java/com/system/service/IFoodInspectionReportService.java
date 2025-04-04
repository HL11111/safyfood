package com.system.service;

import com.system.pojo.FoodInspectionReport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ʳƷ???ⱨ??? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IFoodInspectionReportService extends IService<FoodInspectionReport> {
    Result manageReport(Map<String, Object> requestMap);  //需要管理员身份

    Result findAll(Integer pageNum, Integer pageSize, String foodId);  //无需管理员身份

    Result findOne(FoodInspectionReport report);  //无需管理员身份
}
