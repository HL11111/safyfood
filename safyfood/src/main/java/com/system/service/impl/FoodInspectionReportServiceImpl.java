package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.pojo.FoodInspectionReport;

import com.system.mapper.FoodInspectionReportMapper;
import com.system.pojo.InspectionReport;
import com.system.service.IFoodInspectionReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ʳƷ???ⱨ??? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class FoodInspectionReportServiceImpl extends ServiceImpl<FoodInspectionReportMapper, FoodInspectionReport> implements IFoodInspectionReportService {

    @Autowired
    private FoodInspectionReportMapper reportMapper;

    @Override
    public boolean isExitFoodId(String foodId) {
        LambdaQueryWrapper<FoodInspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FoodInspectionReport::getFoodId, foodId);
        queryWrapper.eq(FoodInspectionReport::getIsDeleted,0);
        Long count = reportMapper.selectCount(queryWrapper);
        return count > 0 ;
    }

    @Override
    public void updateFoodReport(FoodInspectionReport report) {
        // 保存食品报告到数据库
        FoodInspectionReport foodReport = new FoodInspectionReport();
        foodReport.setFoodId(report.getFoodId());
        foodReport.setUserId(report.getUserId());
        foodReport.setInspectionDate(report.getInspectionDate());
        foodReport.setInspectionAgency(report.getInspectionAgency());
        foodReport.setSafetyLevel(report.getSafetyLevel());
        foodReport.setIsPassed(true);
        foodReport.setComplaintId(report.getComplaintId());
        foodReport.setGovernmentId(report.getGovernmentId());
        foodReport.setIsDeleted(0);
        reportMapper.updateById(foodReport);
    }

}
