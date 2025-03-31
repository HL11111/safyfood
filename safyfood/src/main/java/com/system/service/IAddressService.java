package com.system.service;

import com.system.pojo.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ??ַ? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
public interface IAddressService extends IService<Address> {

    Result manageAddress(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(Address address);
}
