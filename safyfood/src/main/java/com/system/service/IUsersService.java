package com.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.system.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ?û?? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IUsersService extends IService<Users> {

    boolean isMerchantExists(Integer userId);

    /* ========================================= */
    Result login(Users user);

    Result regist(Users user,String code);

    Result delete(Users user);

    Result findOne(Users user);

    Result<IPage<Users>> findAll(Integer pageNum, Integer pageSize);

    //管理修改用户和商家信息
    Result updateUserField(Map<String, Object> requestMap);

    //用户和商家修改自身信息
    Result updateSelfInfo(Map<String, Object> requestMap);

    Users getMerchant(int merchantId);

    void updateUser(Users merchant);

    void updateMerchant(Users merchant);

    boolean isGetGreen(int merchantId);

    void getCode(String phone);
}


