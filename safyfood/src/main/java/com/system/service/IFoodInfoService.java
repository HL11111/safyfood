package com.system.service;

import com.system.pojo.FoodInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ʳƷ??Ϣ? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IFoodInfoService extends IService<FoodInfo> {
    Result manageFoodInfo(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(FoodInfo food);
}
