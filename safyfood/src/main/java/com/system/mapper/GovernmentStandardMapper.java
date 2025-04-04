package com.system.mapper;

import com.system.pojo.GovernmentStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * ??????׼? Mapper 接口
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface GovernmentStandardMapper extends BaseMapper<GovernmentStandard> {
    GovernmentStandard selectStandardById(String standardId);
}
