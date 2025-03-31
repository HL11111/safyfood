package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Orders;
import com.system.pojo.Users;
import com.system.mapper.OrdersMapper;
import com.system.service.IOrdersService;
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

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private IUsersService usersService;

    //订单管理接口
    @Override
    public Result manageOrder(Map<String, Object> requestMap) {
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
        //获取操作类型：add-添加新订单，update-更新订单状态
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            //创建新订单
            Orders order = new Orders();
            order.setUserId(userId);
            order.setCreateTime(LocalDateTime.now());
            order.setIsDeleted(0);
            //设置订单信息
            setOrderFields(order, requestMap);
            //保存订单
            int result = ordersMapper.insert(order);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", order.getOrderId());
                data.put("createTime", order.getCreateTime());
                data.put("orderStatus", order.getOrderStatus());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            // 获取要更新的订单ID
            Integer orderId;
            try {
                if (requestMap.get("orderId") instanceof Integer) {
                    orderId = (Integer) requestMap.get("orderId");
                } else {
                    orderId = Integer.parseInt(String.valueOf(requestMap.get("orderId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            //验证订单是否存在且属于该用户
            Orders existingOrder = ordersMapper.selectById(orderId);
            if (existingOrder == null || existingOrder.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingOrder.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            //更新订单信息
            setOrderFields(existingOrder, requestMap);
            existingOrder.setUpdateTime(LocalDateTime.now());
            //保存更新
            int result = ordersMapper.updateById(existingOrder);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", existingOrder.getOrderId());
                data.put("updateTime", existingOrder.getUpdateTime());
                data.put("orderStatus", existingOrder.getOrderStatus());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法：设置订单字段
    private void setOrderFields(Orders order, Map<String, Object> requestMap) {
        if (requestMap.containsKey("foodId")) {
            order.setFoodId(Integer.parseInt(String.valueOf(requestMap.get("foodId"))));
        }
        if (requestMap.containsKey("orderType")) {
            order.setOrderType(String.valueOf(requestMap.get("orderType")));
        }
        if (requestMap.containsKey("orderDetails")) {
            order.setOrderDetails(String.valueOf(requestMap.get("orderDetails")));
        }
        if (requestMap.containsKey("orderPrice")) {
            order.setOrderPrice(new BigDecimal(String.valueOf(requestMap.get("orderPrice"))));
        }
        if (requestMap.containsKey("orderStatus")) {
            order.setOrderStatus(String.valueOf(requestMap.get("orderStatus")));
        }
        if (requestMap.containsKey("orderPhone")) {
            order.setOrderPhone(String.valueOf(requestMap.get("orderPhone")));
        }
        if (requestMap.containsKey("expectTime")) {
            order.setExpectTime(LocalDateTime.parse(String.valueOf(requestMap.get("expectTime"))));
        }
    }

    //分页查询订单信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Orders> ordersPage = new Page<>(pageNum, pageSize);
        //创建查询条件：is_deleted = 0 且 user_id = userId
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getIsDeleted, 0);
        if (userId != null) {
            queryWrapper.eq(Orders::getUserId, userId);
        }
        IPage<Orders> page = ordersMapper.selectPage(ordersPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> orderDataList = new ArrayList<>();
        for (Orders order : page.getRecords()) {
            Map<String, Object> orderDataMap = new HashMap<>();
            Field[] fields = order.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    orderDataMap.put(field.getName(), field.get(order));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            orderDataList.add(orderDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", orderDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一订单信息接口
    @Override
    public Result findOne(Orders order) {
        Orders one = ordersMapper.selectById(order.getOrderId());
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
