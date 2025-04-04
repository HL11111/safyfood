package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.FoodInspectionReport;
import com.system.mapper.FoodInspectionReportMapper;
import com.system.pojo.Users;
import com.system.service.IFoodInspectionReportService;
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
 * ʳƷ???ⱨ??? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class FoodInspectionReportServiceImpl extends ServiceImpl<FoodInspectionReportMapper, FoodInspectionReport> implements IFoodInspectionReportService {
    @Autowired
    private FoodInspectionReportMapper foodInspectionReportMapper;

    @Autowired
    private IUsersService usersService;

    //食品检查报告管理接口(新增不需要,管理需要管理员身份)
    @Override
    public Result manageReport(Map<String, Object> requestMap) {
        //获取用户ID
        Integer userId;
        try {
            if (requestMap.get("userId") instanceof Integer) {
                userId = (Integer) requestMap.get("userId");
            } else {
                userId = Integer.parseInt(String.valueOf(requestMap.get("userId")));
            }
        } catch (NumberFormatException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (userId == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //验证用户是否存在
        Users queryUser = new Users();
        queryUser.setUserId(userId);
        Result userResult = usersService.findOne(queryUser);
        if (userResult.getData() == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证用户类型是否为管理
        Map<String, Object> userData = (Map<String, Object>) userResult.getData();
        String userType = (String) userData.get("userType");
        if (!"管理".equals(userType)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            FoodInspectionReport report = new FoodInspectionReport();
            report.setUserId(userId);
            report.setIsDeleted(0);
            setReportFields(report, requestMap);
            //验证必填字段
            if (report.getFoodId() == null || report.getFoodId().isEmpty() ||
                    report.getInspectionDate() == null || report.getInspectionDate().isEmpty() ||
                    report.getInspectionAgency() == null || report.getInspectionAgency().isEmpty() ||
                    report.getSafetyLevel() == null || report.getSafetyLevel().isEmpty() ||
                    report.getIsPassed() == null) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = foodInspectionReportMapper.insert(report);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("foodId", report.getFoodId());
                data.put("inspectionDate", report.getInspectionDate());
                data.put("isPassed", report.getIsPassed());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            String foodId = String.valueOf(requestMap.get("foodId"));
            if (foodId == null || foodId.isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            //使用复合主键查询，这里简化为按foodId查询，实际可能需要多个字段组合查询
            LambdaQueryWrapper<FoodInspectionReport> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FoodInspectionReport::getFoodId, foodId)
                    .eq(FoodInspectionReport::getIsDeleted, 0);
            FoodInspectionReport existingReport = foodInspectionReportMapper.selectOne(queryWrapper);
            if (existingReport == null) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            setReportFields(existingReport, requestMap);
            int result = foodInspectionReportMapper.update(existingReport, queryWrapper);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("foodId", existingReport.getFoodId());
                data.put("inspectionDate", existingReport.getInspectionDate());
                data.put("isPassed", existingReport.getIsPassed());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置食品检测报告字段
    private void setReportFields(FoodInspectionReport report, Map<String, Object> requestMap) {
        if (requestMap.containsKey("foodId")) {
            report.setFoodId(String.valueOf(requestMap.get("foodId")));
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
        if (requestMap.containsKey("safetyLevel")) {
            report.setSafetyLevel(String.valueOf(requestMap.get("safetyLevel")));
        }
        if (requestMap.containsKey("isPassed")) {
            report.setIsPassed(Boolean.parseBoolean(String.valueOf(requestMap.get("isPassed"))));
        }
        if (requestMap.containsKey("complaintId")) {
            report.setComplaintId(String.valueOf(requestMap.get("complaintId")));
        }
        if (requestMap.containsKey("isDeleted")) {
            report.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询食品检查报告接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, String foodId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<FoodInspectionReport> reportPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FoodInspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FoodInspectionReport::getIsDeleted, 0);
        //按食品ID筛选
        if (foodId != null && !foodId.isEmpty()) {
            queryWrapper.eq(FoodInspectionReport::getFoodId, foodId);
        }
        IPage<FoodInspectionReport> page = foodInspectionReportMapper.selectPage(reportPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> reportDataList = new ArrayList<>();
        for (FoodInspectionReport report : page.getRecords()) {
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

    //查询单一食品检查报告接口
    @Override
    public Result findOne(FoodInspectionReport report) {
        if (report.getFoodId() == null || report.getFoodId().isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        LambdaQueryWrapper<FoodInspectionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FoodInspectionReport::getFoodId, report.getFoodId())
                .eq(FoodInspectionReport::getIsDeleted, 0);
        FoodInspectionReport one = foodInspectionReportMapper.selectOne(queryWrapper);
        if (one == null) {
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
