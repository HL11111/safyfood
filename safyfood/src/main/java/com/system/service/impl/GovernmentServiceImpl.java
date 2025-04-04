package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Government;
import com.system.mapper.GovernmentMapper;
import com.system.pojo.Users;
import com.system.service.IGovernmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ????? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class GovernmentServiceImpl extends ServiceImpl<GovernmentMapper, Government> implements IGovernmentService {
    @Autowired
    private GovernmentMapper governmentMapper;

    @Autowired
    private IUsersService usersService;

    //政府机构管理接口(需要管理员身份)
    @Override
    public Result manageGovernment(Map<String, Object> requestMap) {
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
            Government government = new Government();
            government.setCreateTime(LocalDateTime.now());
            government.setIsDeleted(0);
            setGovernmentFields(government, requestMap);
            //验证必填字段
            if (government.getGovernmentId() == null || government.getGovernmentId().isEmpty() ||
                    government.getGovernmentName() == null || government.getGovernmentName().isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = governmentMapper.insert(government);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("governmentId", government.getGovernmentId());
                data.put("governmentName", government.getGovernmentName());
                data.put("createTime", government.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            String governmentId = String.valueOf(requestMap.get("governmentId"));
            if (governmentId == null || governmentId.isEmpty()) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            Government existingGovernment = governmentMapper.selectGovernmentById(governmentId);
            if (existingGovernment == null || existingGovernment.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            setGovernmentFields(existingGovernment, requestMap);
            //使用wrapper更新而不是updateById
            LambdaQueryWrapper<Government> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Government::getGovernmentId, governmentId)
                    .eq(Government::getIsDeleted, 0);
            int result = governmentMapper.update(existingGovernment, queryWrapper);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("governmentId", existingGovernment.getGovernmentId());
                data.put("governmentName", existingGovernment.getGovernmentName());
                return Result.ok(data);
            }
        }

        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置政府实体类字段
    private void setGovernmentFields(Government government, Map<String, Object> requestMap) {
        if (requestMap.containsKey("governmentId")) {
            government.setGovernmentId(String.valueOf(requestMap.get("governmentId")));
        }
        if (requestMap.containsKey("governmentName")) {
            government.setGovernmentName(String.valueOf(requestMap.get("governmentName")));
        }
        if (requestMap.containsKey("governmentCity")) {
            government.setGovernmentCity(String.valueOf(requestMap.get("governmentCity")));
        }
        if (requestMap.containsKey("governmentPhone")) {
            government.setGovernmentPhone(String.valueOf(requestMap.get("governmentPhone")));
        }
        if (requestMap.containsKey("governmentEmail")) {
            government.setGovernmentEmail(String.valueOf(requestMap.get("governmentEmail")));
        }
        if (requestMap.containsKey("createTime")) {
            government.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            government.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询政府机构接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Government> governmentPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Government> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Government::getIsDeleted, 0);
        IPage<Government> page = governmentMapper.selectPage(governmentPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> governmentDataList = new ArrayList<>();
        for (Government government : page.getRecords()) {
            Map<String, Object> governmentDataMap = new HashMap<>();
            Field[] fields = government.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    governmentDataMap.put(field.getName(), field.get(government));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            governmentDataList.add(governmentDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", governmentDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一政府机构接口
    @Override
    public Result findOne(Government government) {
        if (government.getGovernmentId() == null || government.getGovernmentId().isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        Government one = governmentMapper.selectGovernmentById(government.getGovernmentId());
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
