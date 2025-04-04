package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.GovernmentStandard;
import com.system.mapper.GovernmentStandardMapper;
import com.system.pojo.Users;
import com.system.service.IGovernmentStandardService;
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
 * ??????׼? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class GovernmentStandardServiceImpl extends ServiceImpl<GovernmentStandardMapper, GovernmentStandard> implements IGovernmentStandardService {
    @Autowired
    private GovernmentStandardMapper governmentStandardMapper;

    @Autowired
    private IUsersService usersService;

    //政府标准管理接口(需要管理员身份)
    @Override
    public Result manageStandard(Map<String, Object> requestMap) {
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
            GovernmentStandard standard = new GovernmentStandard();
            standard.setIsDeleted(0);
            setStandardFields(standard, requestMap);
            //验证必填字段
            if (standard.getStandardId() == null || standard.getStandardId().isEmpty() ||
                    standard.getGovernmentId() == null || standard.getGovernmentId().isEmpty() ||
                    standard.getStandardName() == null || standard.getStandardName().isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = governmentStandardMapper.insert(standard);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("standardId", standard.getStandardId());
                data.put("governmentId", standard.getGovernmentId());
                data.put("standardName", standard.getStandardName());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            String standardId = String.valueOf(requestMap.get("standardId"));
            if (standardId == null || standardId.isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            GovernmentStandard existingStandard = governmentStandardMapper.selectStandardById(standardId);
            if (existingStandard == null || existingStandard.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            setStandardFields(existingStandard, requestMap);
            //使用wrapper更新而不是updateById
            LambdaQueryWrapper<GovernmentStandard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GovernmentStandard::getStandardId, standardId)
                    .eq(GovernmentStandard::getIsDeleted, 0);
            int result = governmentStandardMapper.update(existingStandard, queryWrapper);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("standardId", existingStandard.getStandardId());
                data.put("standardName", existingStandard.getStandardName());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置政府标准字段
    private void setStandardFields(GovernmentStandard standard, Map<String, Object> requestMap) {
        if (requestMap.containsKey("standardId")) {
            standard.setStandardId(String.valueOf(requestMap.get("standardId")));
        }
        if (requestMap.containsKey("governmentId")) {
            standard.setGovernmentId(String.valueOf(requestMap.get("governmentId")));
        }
        if (requestMap.containsKey("standardName")) {
            standard.setStandardName(String.valueOf(requestMap.get("standardName")));
        }
        if (requestMap.containsKey("description")) {
            standard.setDescription(String.valueOf(requestMap.get("description")));
        }
        if (requestMap.containsKey("validityPeriod")) {
            standard.setValidityPeriod(Integer.parseInt(String.valueOf(requestMap.get("validityPeriod"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            standard.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询政府标准接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, String governmentId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<GovernmentStandard> standardPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GovernmentStandard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GovernmentStandard::getIsDeleted, 0);
        //按政府机构ID筛选
        if (governmentId != null && !governmentId.isEmpty()) {
            queryWrapper.eq(GovernmentStandard::getGovernmentId, governmentId);
        }
        IPage<GovernmentStandard> page = governmentStandardMapper.selectPage(standardPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> standardDataList = new ArrayList<>();
        for (GovernmentStandard standard : page.getRecords()) {
            Map<String, Object> standardDataMap = new HashMap<>();
            Field[] fields = standard.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    standardDataMap.put(field.getName(), field.get(standard));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            standardDataList.add(standardDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", standardDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一政府标准接口
    @Override
    public Result findOne(GovernmentStandard standard) {
        if (standard.getStandardId() == null || standard.getStandardId().isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        GovernmentStandard one = governmentStandardMapper.selectStandardById(standard.getStandardId());
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
