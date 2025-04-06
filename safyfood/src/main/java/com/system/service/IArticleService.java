package com.system.service;

import com.system.pojo.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ???± 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface IArticleService extends IService<Article> {
    Result manageArticle(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId);

    Result findOne(Article article);
}
