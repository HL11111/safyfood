package com.system.service;

import com.system.pojo.Government;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ????? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IGovernmentService extends IService<Government> {
    Result manageGovernment(Map<String, Object> requestMap);  //需要管理员身份
    Result findAll(Integer pageNum, Integer pageSize);  //无需管理员身份
    Result findOne(Government government);  //无需管理员身份
}
