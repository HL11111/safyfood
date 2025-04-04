package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Orders;
import com.system.pojo.ServiceInfo;
import com.system.mapper.ServiceInfoMapper;
import com.system.pojo.Users;
import com.system.service.IServiceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ??????Ϣ? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@Service
public class ServiceInfoServiceImpl extends ServiceImpl<ServiceInfoMapper, ServiceInfo> implements IServiceInfoService {
    @Autowired
    private ServiceInfoMapper serviceInfoMapper;

    @Autowired
    private IUsersService usersService;

    //服务信息管理接口
    @Override
    public Result manageService(Map<String, Object> requestMap) {
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
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setUserId(userId);
            serviceInfo.setCreateTime(LocalDateTime.now());
            serviceInfo.setIsDeleted(0);
            setServiceFields(serviceInfo, requestMap);
            int result = serviceInfoMapper.insert(serviceInfo);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceId", serviceInfo.getServiceId());
                data.put("createTime", serviceInfo.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer serviceId;
            try {
                if (requestMap.get("serviceId") instanceof Integer) {
                    serviceId = (Integer) requestMap.get("serviceId");
                } else {
                    serviceId = Integer.parseInt(String.valueOf(requestMap.get("serviceId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            ServiceInfo existingService = serviceInfoMapper.selectById(serviceId);
            if (existingService == null || existingService.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingService.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setServiceFields(existingService, requestMap);
            existingService.setUpdateTime(LocalDateTime.now());
            int result = serviceInfoMapper.updateById(existingService);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceId", existingService.getServiceId());
                data.put("updateTime", existingService.getUpdateTime());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法:设置服务信息字段
    private void setServiceFields(ServiceInfo serviceInfo, Map<String, Object> requestMap) {
        if (requestMap.containsKey("serviceName")) {
            serviceInfo.setServiceName(String.valueOf(requestMap.get("serviceName")));
        }
        if (requestMap.containsKey("serviceDescription")) {
            serviceInfo.setServiceDescription(String.valueOf(requestMap.get("serviceDescription")));
        }
        if (requestMap.containsKey("serviceType")) {
            serviceInfo.setServiceType(String.valueOf(requestMap.get("serviceType")));
        }
        if (requestMap.containsKey("serviceStatus")) {
            serviceInfo.setServiceStatus(String.valueOf(requestMap.get("serviceStatus")));
        }
        if (requestMap.containsKey("servicePhone")) {
            serviceInfo.setServicePhone(String.valueOf(requestMap.get("servicePhone")));
        }
        if (requestMap.containsKey("createTime")) {
            serviceInfo.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("updateTime")) {
            serviceInfo.setUpdateTime(LocalDateTime.parse(String.valueOf(requestMap.get("updateTime"))));
        }
        if (requestMap.containsKey("articleId")) {
            serviceInfo.setArticleId(Integer.parseInt(String.valueOf(requestMap.get("articleId"))));
        }
        if (requestMap.containsKey("foodId")) {
            serviceInfo.setFoodId(String.valueOf(requestMap.get("foodId")));
        }
        if (requestMap.containsKey("isDeleted")) {
            serviceInfo.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询服务信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ServiceInfo> servicePage = new Page<>(pageNum, pageSize);
        //创建查询条件：is_deleted = 0 且 user_id = userId
        LambdaQueryWrapper<ServiceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceInfo::getIsDeleted, 0)
                .orderByDesc(ServiceInfo::getCreateTime); // 按创建时间降序排序
        if (userId != null) {
            queryWrapper.eq(ServiceInfo::getUserId, userId);
        }
        IPage<ServiceInfo> page = serviceInfoMapper.selectPage(servicePage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> serviceDataList = new ArrayList<>();
        for (ServiceInfo service : page.getRecords()) {
            Map<String, Object> serviceDataMap = new HashMap<>();
            Field[] fields = service.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    serviceDataMap.put(field.getName(), field.get(service));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            serviceDataList.add(serviceDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", serviceDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一服务信息接口
    @Override
    public Result findOne(ServiceInfo serviceInfo) {
        if (serviceInfo.getServiceId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        ServiceInfo one = serviceInfoMapper.selectById(serviceInfo.getServiceId());
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
