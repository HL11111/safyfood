package com.system.mapper;

import com.system.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * ?û?? Mapper 接口
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface UsersMapper extends BaseMapper<Users> {

    Users selectByUserName(String userName);

    Users selectByUserId(Integer userId);

    Long selectCountByUserName(String userName);

    Long selectCountByUserId(String userId);

    //逻辑删除
    Long isDeletedByUserId(Integer userId);
}
