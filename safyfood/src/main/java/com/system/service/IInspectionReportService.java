package com.system.service;

import com.system.pojo.InspectionReport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.pojo.Vo.InspectionReportVo;

/**
 * <p>
 * ?̼Ҽ??ⱨ??? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IInspectionReportService extends IService<InspectionReport> {

    boolean isExitReport(String reportId);

    void updateStatus(String reportId);

    boolean isExitReportByMerchant(int merchantIdStr);

    void updateInspection(InspectionReportVo report);
}
