package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.mapper.UsersMapper;
import com.system.pojo.InspectionReport;
import com.system.mapper.InspectionReportMapper;
import com.system.pojo.Users;
import com.system.service.IInspectionReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private IUsersService usersService;

    //商家检查报告管理接口(需要管理员身份)
    @Override
    public Result manageReport(Map<String, Object> requestMap) {
        //获取管理员ID
        Integer adminUserId;
        try {
            if (requestMap.get("adminUserId") instanceof Integer) {
                adminUserId = (Integer) requestMap.get("adminUserId");
            } else {
                adminUserId = Integer.parseInt(String.valueOf(requestMap.get("adminUserId")));
            }
        } catch (NumberFormatException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (adminUserId == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //验证管理员是否存在
        Users queryAdmin = new Users();
        queryAdmin.setUserId(adminUserId);
        Result adminResult = usersService.findOne(queryAdmin);
        if (adminResult.getData() == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证用户类型是否为管理
        Map<String, Object> adminData = (Map<String, Object>) adminResult.getData();
        String adminType = (String) adminData.get("userType");
        if (!"管理".equals(adminType)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        //获取目标商家的用户名
        String targetUserName = String.valueOf(requestMap.get("targetUserName"));
        if (targetUserName == null || targetUserName.isEmpty() || targetUserName.equals("null")) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //根据用户名查询目标用户
        Users targetUser = usersMapper.selectByUserName(targetUserName);
        if (targetUser == null || targetUser.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证目标用户是否为商家
        if (!"商家".equals(targetUser.getUserType())) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            InspectionReport report = new InspectionReport();
            report.setUserId(targetUser.getUserId());  // 设置为目标商家的ID
            report.setIsDeleted(0);
            setReportFields(report, requestMap);
            //验证必填字段
            if (report.getReportId() == null || report.getReportId().isEmpty() ||
                    report.getInspectionDate() == null || report.getInspectionDate().isEmpty() ||
                    report.getInspectionAgency() == null || report.getInspectionAgency().isEmpty() ||
                    report.getSafetyLevel() == null || report.getSafetyLevel().isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = inspectionReportMapper.insert(report);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("reportId", report.getReportId());
                data.put("userId", report.getUserId());
                data.put("targetUserName", targetUserName);
                data.put("inspectionDate", report.getInspectionDate());
                data.put("isPassed", report.getIsPassed());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            String reportId = String.valueOf(requestMap.get("reportId"));
            if (reportId == null || reportId.isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            InspectionReport existingReport = inspectionReportMapper.selectByReportId(reportId);
            if (existingReport == null || existingReport.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            //验证报告是否属于目标商家
            if (!existingReport.getUserId().equals(targetUser.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setReportFields(existingReport, requestMap);
            //使用wrapper更新而不是updateById
            LambdaQueryWrapper<InspectionReport> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InspectionReport::getReportId, reportId)
                    .eq(InspectionReport::getIsDeleted, 0);
            System.out.println(queryWrapper.toString());
            int result = inspectionReportMapper.update(existingReport, queryWrapper);
            System.out.println(result);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("reportId", existingReport.getReportId());
                data.put("userId", existingReport.getUserId());
                data.put("targetUserName", targetUserName);
                data.put("inspectionDate", existingReport.getInspectionDate());
                data.put("isPassed", existingReport.getIsPassed());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法啊: 设置商家检测报告字段
    private void setReportFields(InspectionReport report, Map<String, Object> requestMap) {
        if (requestMap.containsKey("reportId")) {
            report.setReportId(String.valueOf(requestMap.get("reportId")));
        }
        if (requestMap.containsKey("governmentId")) {
            report.setGovernmentId(String.valueOf(requestMap.get("governmentId")));
        }
        if (requestMap.containsKey("inspectionDate")) {
            report.setInspectionDate(String.valueOf(requestMap.get("inspectionDate")));
        }
        if (requestMap.containsKey("inspectionAgency")) {
            report.setInspectionAgency(String.valueOf(requestMap.get("inspectionAgency")));
        }
        if (requestMap.containsKey("safetyStandard")) {
            report.setSafetyStandard(String.valueOf(requestMap.get("safetyStandard")));
        }
        if (requestMap.containsKey("safetyLevel")) {
            report.setSafetyLevel(String.valueOf(requestMap.get("safetyLevel")));
        }
        if (requestMap.containsKey("isPassed")) {
            report.setIsPassed(Boolean.parseBoolean(String.valueOf(requestMap.get("isPassed"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            report.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询商家检查报告接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<InspectionReport> reportPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InspectionReport::getIsDeleted, 0);

        //按商家用户ID筛选
        if (userId != null) {
            queryWrapper.eq(InspectionReport::getUserId, userId);
        }
        IPage<InspectionReport> page = inspectionReportMapper.selectPage(reportPage, queryWrapper);

        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> reportDataList = new ArrayList<>();
        for (InspectionReport report : page.getRecords()) {
            Map<String, Object> reportDataMap = new HashMap<>();
            Field[] fields = report.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    reportDataMap.put(field.getName(), field.get(report));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            reportDataList.add(reportDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", reportDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());

        return Result.ok(data);
    }

    //查询单一商家检查报告接口
    @Override
    public Result findOne(InspectionReport report) {
        if (report.getReportId() == null || report.getReportId().isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        InspectionReport one = inspectionReportMapper.selectByReportId(report.getReportId());
        if (one == null || one.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        Map<String, Object> data = new HashMap<>();
        Field[] fields = one.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                data.put(field.getName(), field.get(one));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Result.ok(data);
    }
}
