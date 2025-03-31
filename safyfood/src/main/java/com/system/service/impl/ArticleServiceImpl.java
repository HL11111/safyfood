package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Article;
import com.system.mapper.ArticleMapper;
import com.system.pojo.Users;
import com.system.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * <p>
 * ???± 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private IUsersService usersService;

    //文章管理接口
    @Override
    public Result manageArticle(Map<String, Object> requestMap) {
        //获取用户ID
        Integer userId;
        try {
            if (requestMap.get("userId") instanceof Integer) {
                userId = (Integer) requestMap.get("userId");
            } else {
                userId = Integer.parseInt(String.valueOf(requestMap.get("userId")));
            }
        } catch (NumberFormatException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (userId == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //验证用户是否存在
        Users queryUser = new Users();
        queryUser.setUserId(userId);
        Result userResult = usersService.findOne(queryUser);
        if (userResult.getData() == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            Article article = new Article();
            article.setUserId(userId);
            article.setPublishTime(LocalDateTime.now());
            article.setIsDeleted(0);
            article.setLikeCount(0);
            article.setDislikeCount(0);
            article.setCommentCount(0);
            article.setArticleStatus("待审核");
            setArticleFields(article, requestMap);
            int result = articleMapper.insert(article);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("articleId", article.getArticleId());
                data.put("articleTitle", article.getArticleTitle());
                data.put("publishTime", article.getPublishTime());
                data.put("articleStatus", article.getArticleStatus());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer articleId;
            try {
                if (requestMap.get("articleId") instanceof Integer) {
                    articleId = (Integer) requestMap.get("articleId");
                } else {
                    articleId = Integer.parseInt(String.valueOf(requestMap.get("articleId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            Article existingArticle = articleMapper.selectById(articleId);
            if (existingArticle == null || existingArticle.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingArticle.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setArticleFields(existingArticle, requestMap);
            existingArticle.setArticleStatus("待审核"); // 修改后需要重新审核
            int result = articleMapper.updateById(existingArticle);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("articleId", existingArticle.getArticleId());
                data.put("articleTitle", existingArticle.getArticleTitle());
                data.put("articleStatus", existingArticle.getArticleStatus());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置文章字段
    private void setArticleFields(Article article, Map<String, Object> requestMap) {
        if (requestMap.containsKey("articleTitle")) {
            article.setArticleTitle(String.valueOf(requestMap.get("articleTitle")));
        }
        if (requestMap.containsKey("articleType")) {
            article.setArticleType(String.valueOf(requestMap.get("articleType")));
        }
        if (requestMap.containsKey("articleContent")) {
            article.setArticleContent(String.valueOf(requestMap.get("articleContent")));
        }
        if (requestMap.containsKey("articleAuthor")) {
            article.setArticleAuthor(String.valueOf(requestMap.get("articleAuthor")));
        }
        if (requestMap.containsKey("cityOrigin")) {
            article.setCityOrigin(String.valueOf(requestMap.get("cityOrigin")));
        }
    }

    //分页查询文章信息接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Article> articlePage = new Page<>(pageNum, pageSize);
        //创建查询条件：is_deleted = 0 且 user_id = userId (如果提供)
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getIsDeleted, 0)
                .orderByDesc(Article::getPublishTime); //按发布时间降序排序
        if (userId != null) {
            queryWrapper.eq(Article::getUserId, userId);
        }
        IPage<Article> page = articleMapper.selectPage(articlePage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> articleDataList = new ArrayList<>();
        for (Article article : page.getRecords()) {
            Map<String, Object> articleDataMap = new HashMap<>();
            Field[] fields = article.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    articleDataMap.put(field.getName(), field.get(article));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            articleDataList.add(articleDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", articleDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一文章信息接口
    @Override
    public Result findOne(Article article) {
        if (article.getArticleId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        Article one = articleMapper.selectById(article.getArticleId());
        if (one == null || one.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        Map<String, Object> data = new HashMap<>();
        Field[] fields = one.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                data.put(field.getName(), field.get(one));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Result.ok(data);
    }
}

