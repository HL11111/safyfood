package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Address;
import com.system.mapper.AddressMapper;
import com.system.pojo.Users;
import com.system.service.IAddressService;
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
 * ??ַ? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private IUsersService usersService;


    //用户管理地址信息接口
    @Override
    public Result manageAddress(Map<String, Object> requestMap) {
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
        //获取操作类型：add-添加新地址，update-更新已有地址
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            //创建新的地址信息
            Address address = new Address();
            address.setUserId(userId);
            address.setCreateTime(LocalDateTime.now());
            address.setIsDeleted(0);
            //设置地址信息的各个字段
            setAddressFields(address, requestMap);
            //保存地址信息
            int result = addressMapper.insert(address);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("addressId", address.getAddressId());
                data.put("createTime", address.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            //获取要更新的地址ID
            Integer addressId;
            try {
                if (requestMap.get("addressId") instanceof Integer) {
                    addressId = (Integer) requestMap.get("addressId");
                } else {
                    addressId = Integer.parseInt(String.valueOf(requestMap.get("addressId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            //验证地址是否存在且属于该用户
            Address existingAddress = addressMapper.selectById(addressId);
            if (existingAddress == null || existingAddress.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingAddress.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            //更新地址信息字段
            setAddressFields(existingAddress, requestMap);
            existingAddress.setUpdateTime(LocalDateTime.now());
            //保存更新,数据持久化
            int result = addressMapper.updateById(existingAddress);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("addressId", existingAddress.getAddressId());
                data.put("updateTime", existingAddress.getUpdateTime());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    // 辅助方法：设置地址信息字段
    private void setAddressFields(Address address, Map<String, Object> requestMap) {
        if (requestMap.containsKey("province")) {
            address.setProvince(String.valueOf(requestMap.get("province")));
        }
        if (requestMap.containsKey("city")) {
            address.setCity(String.valueOf(requestMap.get("city")));
        }
        if (requestMap.containsKey("addressDetails")) {
            address.setAddressDetails(String.valueOf(requestMap.get("addressDetails")));
        }
        if (requestMap.containsKey("postalCode")) {
            address.setPostalCode(String.valueOf(requestMap.get("postalCode")));
        }
        if (requestMap.containsKey("contactPhone")) {
            address.setContactPhone(String.valueOf(requestMap.get("contactPhone")));
        }
    }


    //分页查询地址信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Address> addressPage = new Page<>(pageNum, pageSize);
        //创建查询条件：is_deleted = 0 且 user_id = userId
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getIsDeleted, 0);
        if (userId != null) {
            queryWrapper.eq(Address::getUserId, userId);
        }
        IPage<Address> page = addressMapper.selectPage(addressPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> addressDataList = new ArrayList<>();
        for (Address address : page.getRecords()) {
            Map<String, Object> addressDataMap = new HashMap<>();
            Field[] fields = address.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    addressDataMap.put(field.getName(), field.get(address));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            addressDataList.add(addressDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", addressDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());

        return Result.ok(data);
    }


    //查询单一地址信息接口
    @Override
    public Result findOne(Address address) {
        Address one = addressMapper.selectById(address.getAddressId());
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

