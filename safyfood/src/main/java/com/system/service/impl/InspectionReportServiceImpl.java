package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.system.pojo.InspectionReport;
import com.system.mapper.InspectionReportMapper;
import com.system.pojo.Vo.InspectionReportVo;
import com.system.service.IInspectionReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ?̼Ҽ??ⱨ??? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class InspectionReportServiceImpl extends ServiceImpl<InspectionReportMapper, InspectionReport> implements IInspectionReportService {

    @Autowired
    private InspectionReportMapper inspectionReportMapper;

    @Override
    public boolean isExitReport(String reportId) {
        //逻辑 当表中存在该报告id 并且 报告是没有删除的 -> count > 0
        LambdaQueryWrapper<InspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InspectionReport::getReportId, reportId);
        queryWrapper.eq(InspectionReport::getIsDeleted,0);
        Long count = inspectionReportMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public void updateStatus(String reportId) {
        //逻辑 本地id = 参数id -> 逻辑删除is_deleted
        LambdaUpdateWrapper<InspectionReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(InspectionReport::getReportId, reportId);
        updateWrapper.set(InspectionReport::getIsDeleted,1);
        inspectionReportMapper.update(null, updateWrapper);
    }

    @Override
    public boolean isExitReportByMerchant(int merchantId) {
        LambdaQueryWrapper<InspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InspectionReport::getUserId, merchantId);
        queryWrapper.eq(InspectionReport::getIsPassed,true);
        Long count = inspectionReportMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public void updateInspection(InspectionReportVo report) {
        //保存商家报告到数据库
        InspectionReport inspectionReport = new InspectionReport();
        inspectionReport.setReportId(report.getReportId());
        inspectionReport.setUserId(report.getUserId());
        inspectionReport.setInspectionDate(report.getInspectionDate());
        inspectionReport.setInspectionAgency(report.getInspectionAgency());
        inspectionReport.setSafetyLevel(report.getSafetyLevel());
        inspectionReport.setIsPassed(true);
        inspectionReport.setGovernmentId(report.getGovernmentId());
        inspectionReport.setIsDeleted(0);

        inspectionReportMapper.updateById(inspectionReport);
    }
}




