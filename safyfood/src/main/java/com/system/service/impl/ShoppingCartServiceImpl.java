package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.ShoppingCart;
import com.system.mapper.ShoppingCartMapper;
import com.system.pojo.Users;
import com.system.service.IShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ???ﳵ? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private IUsersService usersService;

    //购物车管理接口
    @Override
    public Result manageCart(Map<String, Object> requestMap) {
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
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            cart.setIsDeleted(0);
            cart.setIsChecked(false);
            setCartFields(cart, requestMap);
            int result = shoppingCartMapper.insert(cart);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("shoppingId", cart.getShoppingId());
                data.put("createTime", cart.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer shoppingId;
            try {
                if (requestMap.get("shoppingId") instanceof Integer) {
                    shoppingId = (Integer) requestMap.get("shoppingId");
                } else {
                    shoppingId = Integer.parseInt(String.valueOf(requestMap.get("shoppingId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            ShoppingCart existingCart = shoppingCartMapper.selectById(shoppingId);
            if (existingCart == null || existingCart.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingCart.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setCartFields(existingCart, requestMap);
            int result = shoppingCartMapper.updateById(existingCart);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("shoppingId", existingCart.getShoppingId());
                data.put("foodQuantity", existingCart.getFoodQuantity());
                data.put("totalPrice", existingCart.getTotalPrice());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法:设置购物车字段
    private void setCartFields(ShoppingCart cart, Map<String, Object> requestMap) {
        if (requestMap.containsKey("foodId")) {
            cart.setFoodId(Integer.parseInt(String.valueOf(requestMap.get("foodId"))));
        }
        if (requestMap.containsKey("foodName")) {
            cart.setFoodName(String.valueOf(requestMap.get("foodName")));
        }
        if (requestMap.containsKey("foodDescription")) {
            cart.setFoodDescription(String.valueOf(requestMap.get("foodDescription")));
        }
        if (requestMap.containsKey("foodPrice")) {
            cart.setFoodPrice(new BigDecimal(String.valueOf(requestMap.get("foodPrice"))));
        }
        if (requestMap.containsKey("foodQuantity")) {
            cart.setFoodQuantity(Integer.parseInt(String.valueOf(requestMap.get("foodQuantity"))));
        }
        if (requestMap.containsKey("totalPrice")) {
            cart.setTotalPrice(new BigDecimal(String.valueOf(requestMap.get("totalPrice"))));
        }
        if (requestMap.containsKey("isChecked")) {
            cart.setIsChecked(Boolean.parseBoolean(String.valueOf(requestMap.get("isChecked"))));
        }
        if (requestMap.containsKey("shoppingStatus")) {
            cart.setShoppingStatus(String.valueOf(requestMap.get("shoppingStatus")));
        }
    }

    //分页查询购物车信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ShoppingCart> cartPage = new Page<>(pageNum, pageSize);

        //创建查询条件：is_deleted = 0 且 user_id = userId
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getIsDeleted, 0)
                .orderByDesc(ShoppingCart::getCreateTime); //按创建时间降序排序
        if (userId != null) {
            queryWrapper.eq(ShoppingCart::getUserId, userId);
        }
        IPage<ShoppingCart> page = shoppingCartMapper.selectPage(cartPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> cartDataList = new ArrayList<>();
        for (ShoppingCart cart : page.getRecords()) {
            Map<String, Object> cartDataMap = new HashMap<>();
            Field[] fields = cart.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    cartDataMap.put(field.getName(), field.get(cart));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            cartDataList.add(cartDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", cartDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一购物车信息接口
    @Override
    public Result findOne(ShoppingCart shoppingCart) {
        if (shoppingCart.getShoppingId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        ShoppingCart one = shoppingCartMapper.selectById(shoppingCart.getShoppingId());
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
