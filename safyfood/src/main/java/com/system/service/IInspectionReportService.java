package com.system.service;

import com.system.pojo.InspectionReport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ?̼Ҽ??ⱨ??? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IInspectionReportService extends IService<InspectionReport> {
    Result manageReport(Map<String, Object> requestMap);  //需要管理员身份

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);  //无需管理员身份

    Result findOne(InspectionReport report);  //无需管理员身份
}
