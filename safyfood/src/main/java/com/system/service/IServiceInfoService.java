package com.system.service;

import com.system.pojo.Orders;
import com.system.pojo.ServiceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ??????Ϣ? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
public interface IServiceInfoService extends IService<ServiceInfo> {
    Result manageService(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(ServiceInfo serviceInfo);
}
