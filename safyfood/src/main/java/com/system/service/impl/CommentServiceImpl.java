package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Comment;
import com.system.mapper.CommentMapper;
import com.system.pojo.Users;
import com.system.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ???۱ 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private IUsersService usersService;

    //评论管理接口
    @Override
    public Result manageComment(Map<String, Object> requestMap) {
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
            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setCreateTime(LocalDateTime.now());
            comment.setIsDeleted(0);
            comment.setLikeCount(0);
            comment.setCommentStatus("待审核");
            //获取用户名
            Map<String, Object> userData = (Map<String, Object>) userResult.getData();
            comment.setUserName((String) userData.get("userName"));
            setCommentFields(comment, requestMap);
            //验证评论类型和目标ID
            if (comment.getCommentType() == null ||
                    (!"文章评论".equals(comment.getCommentType()) &&
                            !"食品评论".equals(comment.getCommentType()))) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            if ("文章评论".equals(comment.getCommentType()) && comment.getArticleId() == null) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            if ("食品评论".equals(comment.getCommentType()) && comment.getFoodId() == null) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            int result = commentMapper.insert(comment);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("commentId", comment.getCommentId());
                data.put("commentType", comment.getCommentType());
                data.put("commentContent", comment.getCommentContent());
                data.put("createTime", comment.getCreateTime());
                data.put("commentStatus", comment.getCommentStatus());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer commentId;
            try {
                if (requestMap.get("commentId") instanceof Integer) {
                    commentId = (Integer) requestMap.get("commentId");
                } else {
                    commentId = Integer.parseInt(String.valueOf(requestMap.get("commentId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            Comment existingComment = commentMapper.selectById(commentId);
            if (existingComment == null || existingComment.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            if (!userId.equals(existingComment.getUserId())) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
            setCommentFields(existingComment, requestMap);
            existingComment.setCommentStatus("待审核"); // 修改评论后需要重新审核
            int result = commentMapper.updateById(existingComment);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("commentId", existingComment.getCommentId());
                data.put("commentContent", existingComment.getCommentContent());
                data.put("commentStatus", existingComment.getCommentStatus());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法:设置文章反馈字段
    private void setCommentFields(Comment comment, Map<String, Object> requestMap) {
        if (requestMap.containsKey("articleId")) {
            comment.setArticleId(Integer.parseInt(String.valueOf(requestMap.get("articleId"))));
        }
        if (requestMap.containsKey("foodId")) {
            comment.setFoodId(Integer.parseInt(String.valueOf(requestMap.get("foodId"))));
        }
        if (requestMap.containsKey("userName")) {
            comment.setUserName(String.valueOf(requestMap.get("userName")));
        }
        if (requestMap.containsKey("commentType")) {
            comment.setCommentType(String.valueOf(requestMap.get("commentType")));
        }
        if (requestMap.containsKey("commentContent")) {
            comment.setCommentContent(String.valueOf(requestMap.get("commentContent")));
        }
        if (requestMap.containsKey("likeCount")) {
            comment.setLikeCount(Integer.parseInt(String.valueOf(requestMap.get("likeCount"))));
        }
        if (requestMap.containsKey("commentStatus")) {
            comment.setCommentStatus(String.valueOf(requestMap.get("commentStatus")));
        }
        if (requestMap.containsKey("createTime")) {
            comment.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("aduitBy")) {
            comment.setAduitBy(String.valueOf(requestMap.get("aduitBy")));
        }
        if (requestMap.containsKey("isDeleted")) {
            comment.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询评论信息接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId, String commentType, Integer targetId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Comment> commentPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime);
        //根据用户ID查询
        if (userId != null) {
            queryWrapper.eq(Comment::getUserId, userId);
        }
        //根据评论类型查询
        if (commentType != null) {
            queryWrapper.eq(Comment::getCommentType, commentType);
            //根据目标ID查询
            if (targetId != null) {
                if ("文章评论".equals(commentType)) {
                    queryWrapper.eq(Comment::getArticleId, targetId);
                } else if ("食品评论".equals(commentType)) {
                    queryWrapper.eq(Comment::getFoodId, targetId);
                }
            }
        }
        IPage<Comment> page = commentMapper.selectPage(commentPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> commentDataList = new ArrayList<>();
        for (Comment comment : page.getRecords()) {
            Map<String, Object> commentDataMap = new HashMap<>();
            Field[] fields = comment.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    commentDataMap.put(field.getName(), field.get(comment));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            commentDataList.add(commentDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", commentDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一评论信息接口
    @Override
    public Result findOne(Comment comment) {
        if (comment.getCommentId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        Comment one = commentMapper.selectById(comment.getCommentId());
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

