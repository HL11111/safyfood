package com.system.service;

import com.system.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.utils.Result;

import java.util.Map;

/**
 * <p>
 * ???۱ 服务类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
public interface ICommentService extends IService<Comment> {
    Result manageComment(Map<String, Object> requestMap);

    Result findAll(Integer pageNum, Integer pageSize, Integer userId, String commentType, Integer targetId);

    Result findOne(Comment comment);
}
