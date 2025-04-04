package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.ArticleFeedback;
import com.system.mapper.ArticleFeedbackMapper;
import com.system.pojo.Users;
import com.system.service.IArticleFeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ???·???? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class ArticleFeedbackServiceImpl extends ServiceImpl<ArticleFeedbackMapper, ArticleFeedback> implements IArticleFeedbackService {
    @Autowired
    private ArticleFeedbackMapper articleFeedbackMapper;

    @Autowired
    private IUsersService usersService;

    //文章反馈管理接口
    @Override
    public Result manageFeedback(Map<String, Object> requestMap) {
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
            ArticleFeedback feedback = new ArticleFeedback();
            feedback.setUserId(userId);
            feedback.setCreateTime(LocalDateTime.now());
            feedback.setIsDeleted(0);
            setFeedbackFields(feedback, requestMap);
            //检查反馈类型是否合法
            if (feedback.getFeedbackType() == null ||
                    (feedback.getFeedbackType() != 1 && // 点赞
                            feedback.getFeedbackType() != 2 && // 收藏
                            feedback.getFeedbackType() != 3)) { // 举报
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = articleFeedbackMapper.insert(feedback);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("feedbackId", feedback.getFeedbackId());
                data.put("articleId", feedback.getArticleId());
                data.put("feedbackType", feedback.getFeedbackType());
                data.put("createTime", feedback.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer feedbackId;
            try {
                if (requestMap.get("feedbackId") instanceof Integer) {
                    feedbackId = (Integer) requestMap.get("feedbackId");
                } else {
                    feedbackId = Integer.parseInt(String.valueOf(requestMap.get("feedbackId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            ArticleFeedback existingFeedback = articleFeedbackMapper.selectById(feedbackId);
            if (existingFeedback == null || existingFeedback.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingFeedback.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setFeedbackFields(existingFeedback, requestMap);
            int result = articleFeedbackMapper.updateById(existingFeedback);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("feedbackId", existingFeedback.getFeedbackId());
                data.put("articleId", existingFeedback.getArticleId());
                data.put("feedbackType", existingFeedback.getFeedbackType());
                data.put("reportStatus", existingFeedback.getReportStatus());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置文章反馈字段
    private void setFeedbackFields(ArticleFeedback feedback, Map<String, Object> requestMap) {
        if (requestMap.containsKey("articleId")) {
            feedback.setArticleId(Integer.parseInt(String.valueOf(requestMap.get("articleId"))));
        }
        if (requestMap.containsKey("feedbackType")) {
            feedback.setFeedbackType(Integer.parseInt(String.valueOf(requestMap.get("feedbackType"))));
        }
        if (requestMap.containsKey("favoriteStatus")) {
            feedback.setFavoriteStatus(String.valueOf(requestMap.get("favoriteStatus")));
        }
        if (requestMap.containsKey("reportContent")) {
            feedback.setReportContent(String.valueOf(requestMap.get("reportContent")));
        }
        if (requestMap.containsKey("reportStatus")) {
            feedback.setReportStatus(String.valueOf(requestMap.get("reportStatus")));
        }
        if (requestMap.containsKey("createTime")) {
            feedback.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            feedback.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询文章反馈信息接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ArticleFeedback> feedbackPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ArticleFeedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFeedback::getIsDeleted, 0)
                .orderByDesc(ArticleFeedback::getCreateTime);
        if (userId != null) {
            queryWrapper.eq(ArticleFeedback::getUserId, userId);
        }
        IPage<ArticleFeedback> page = articleFeedbackMapper.selectPage(feedbackPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> feedbackDataList = new ArrayList<>();
        for (ArticleFeedback feedback : page.getRecords()) {
            Map<String, Object> feedbackDataMap = new HashMap<>();
            Field[] fields = feedback.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    feedbackDataMap.put(field.getName(), field.get(feedback));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            feedbackDataList.add(feedbackDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", feedbackDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一文章反馈信息接口
    @Override
    public Result findOne(ArticleFeedback feedback) {
        if (feedback.getFeedbackId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        ArticleFeedback one = articleFeedbackMapper.selectById(feedback.getFeedbackId());
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
