package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.FoodInfo;
import com.system.pojo.Users;
import com.system.mapper.FoodInfoMapper;
import com.system.service.IFoodInfoService;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 食品信息表 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class FoodInfoServiceImpl extends ServiceImpl<FoodInfoMapper, FoodInfo> implements IFoodInfoService {
    @Autowired
    private FoodInfoMapper foodInfoMapper;

    @Autowired
    private IUsersService usersService;

    //商家管理食品信息接口
    @Override
    public Result manageFoodInfo(Map<String, Object> requestMap) {
        //获取商家用户ID
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
        //使用 Users 对象构建查询条件
        Users queryUser = new Users();
        queryUser.setUserId(userId);
        //通过 UsersService 查询用户信息
        Result userResult = usersService.findOne(queryUser);
        if (userResult.getData() == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //从返回的 Result 中获取用户信息
        Map<String, Object> userData = (Map<String, Object>) userResult.getData();
        String userType = (String) userData.get("userType");
        Integer isDeleted = (Integer) userData.get("isDeleted");
        if (isDeleted == 1 || !"商家".equals(userType)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        //获取操作类型：add-添加新食品，update-更新已有食品
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            //创建新的食品信息
            FoodInfo foodInfo = new FoodInfo();
            foodInfo.setUserId(userId);
            foodInfo.setCreateTime(LocalDateTime.now());
            //设置食品信息的各个字段
            setFoodInfoFields(foodInfo, requestMap);
            //保存食品信息
            int result = foodInfoMapper.insert(foodInfo);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("foodId", foodInfo.getFoodId());
                data.put("foodName", foodInfo.getFoodName());
                data.put("createTime", foodInfo.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            // 获取要更新的食品ID
            Integer foodId;
            try {
                if (requestMap.get("foodId") instanceof Integer) {
                    foodId = (Integer) requestMap.get("foodId");
                } else {
                    foodId = Integer.parseInt(String.valueOf(requestMap.get("foodId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            //验证食品是否存在且属于该商家
            FoodInfo existingFood = foodInfoMapper.selectById(foodId);
            if (existingFood == null || existingFood.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingFood.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            //更新食品信息字段
            setFoodInfoFields(existingFood, requestMap);
            existingFood.setUpdateTime(LocalDateTime.now());
            //保存更新
            int result = foodInfoMapper.updateById(existingFood);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("foodId", existingFood.getFoodId());
                data.put("foodName", existingFood.getFoodName());
                data.put("updateTime", existingFood.getUpdateTime());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法：设置食品信息字段
    private void setFoodInfoFields(FoodInfo foodInfo, Map<String, Object> requestMap) {
        if (requestMap.containsKey("foodName")) {
            foodInfo.setFoodName(String.valueOf(requestMap.get("foodName")));
        }
        if (requestMap.containsKey("foodCategory")) {
            foodInfo.setFoodCategory(String.valueOf(requestMap.get("foodCategory")));
        }
        if (requestMap.containsKey("foodBrand")) {
            foodInfo.setFoodBrand(String.valueOf(requestMap.get("foodBrand")));
        }
        if (requestMap.containsKey("foodDescription")) {
            foodInfo.setFoodDescription(String.valueOf(requestMap.get("foodDescription")));
        }
        if (requestMap.containsKey("foodIngredient")) {
            foodInfo.setFoodIngredient(String.valueOf(requestMap.get("foodIngredient")));
        }
        if (requestMap.containsKey("monthSell")) {
            foodInfo.setMonthSell(Integer.parseInt(String.valueOf(requestMap.get("monthSell"))));
        }
        if (requestMap.containsKey("foodStore")) {
            foodInfo.setFoodStore(Integer.parseInt(String.valueOf(requestMap.get("foodStore"))));
        }
        if (requestMap.containsKey("foodPrice")) {
            foodInfo.setFoodPrice(new BigDecimal(String.valueOf(requestMap.get("foodPrice"))));
        }
        if (requestMap.containsKey("foodOrigin")) {
            foodInfo.setFoodOrigin(String.valueOf(requestMap.get("foodOrigin")));
        }
        if (requestMap.containsKey("favoriteStatus")) {
            foodInfo.setFavoriteStatus(Integer.parseInt(String.valueOf(requestMap.get("favoriteStatus"))));
        }
        if (requestMap.containsKey("createTime")) {
            foodInfo.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("updateTime")) {
            foodInfo.setUpdateTime(LocalDateTime.parse(String.valueOf(requestMap.get("updateTime"))));
        }
        if (requestMap.containsKey("rankType")) {
            foodInfo.setRankType(Integer.parseInt(String.valueOf(requestMap.get("rankType"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            foodInfo.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询食品信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        //处理分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        //创建分页对象
        Page<FoodInfo> foodInfoPage = new Page<>(pageNum, pageSize);
        //创建查询条件：is_deleted = 0 且 user_id = userId
        LambdaQueryWrapper<FoodInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FoodInfo::getIsDeleted, 0);
        if (userId != null) {
            queryWrapper.eq(FoodInfo::getUserId, userId);
        }
        //执行分页查询
        IPage<FoodInfo> page = foodInfoMapper.selectPage(foodInfoPage, queryWrapper);
        //检查是否有数据
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        //使用反射获取所有字段信息
        List<Map<String, Object>> foodDataList = new ArrayList<>();
        for (FoodInfo food : page.getRecords()) {
            Map<String, Object> foodDataMap = new HashMap<>();
            Field[] fields = food.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    foodDataMap.put(field.getName(), field.get(food));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            foodDataList.add(foodDataMap);
        }
        //构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("records", foodDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一食品信息接口
    @Override
    public Result findOne(FoodInfo food) {
        FoodInfo one = foodInfoMapper.selectById(food.getFoodId());
        if (one == null || one.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        //构建返回数据
        Map<String, Object> data = new HashMap<>();
        //使用反射获取实体类的所有字段
        Field[] fields = one.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                //将字段名作为键，字段值作为值放入data中
                data.put(field.getName(), field.get(one));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Result.ok(data);
    }
}