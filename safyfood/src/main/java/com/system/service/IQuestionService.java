package com.system.service;

import com.system.pojo.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ??Ŀ? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IQuestionService extends IService<Question> {
    Result manageQuestion(Map<String, Object> requestMap);  //需要管理权限

    Result findAll(Integer pageNum, Integer pageSize, Integer bankId);  //不需要管理权限

    Result findOne(Question question);  //不需要管理权限
}
