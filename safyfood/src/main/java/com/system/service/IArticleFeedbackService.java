package com.system.service;

import com.system.pojo.ArticleFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ???·???? 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IArticleFeedbackService extends IService<ArticleFeedback> {
    Result manageFeedback(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(ArticleFeedback feedback);
}
