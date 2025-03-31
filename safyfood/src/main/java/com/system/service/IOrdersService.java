package com.system.service;

import com.system.pojo.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * ????? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IOrdersService extends IService<Orders> {

    Result manageOrder(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(Orders order);
}
