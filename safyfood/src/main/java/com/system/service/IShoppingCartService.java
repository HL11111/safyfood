package com.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ???ﳵ? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IShoppingCartService extends IService<ShoppingCart> {
    Result manageCart(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(ShoppingCart shoppingCart);
}
