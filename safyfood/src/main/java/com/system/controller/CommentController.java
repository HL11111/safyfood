package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.system.service.ICommentService;
import com.system.pojo.Comment;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ???۱ 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Operation(summary = "评论管理")
    @PostMapping("/manageComment")
    public Result manageComment(@RequestBody Map<String, Object> requestMap) {
        return commentService.manageComment(requestMap);
    }

    @Operation(summary = "分页查询评论")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId,
                          @RequestParam(required = false) String commentType,
                          @RequestParam(required = false) Integer targetId) {
        return commentService.findAll(pageNum, pageSize, userId, commentType, targetId);
    }

    @Operation(summary = "查询单一评论")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Comment comment) {
        return commentService.findOne(comment);
    }
//        @Resource
//        private ICommentService commentService;
//
//        @PostMapping
//        public Boolean save(@RequestBody Comment comment) {
//            return commentService.saveOrUpdate(comment);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return commentService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Comment> findAll() {
//            return commentService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Comment findOne(@PathVariable Integer id) {
//            return commentService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Comment> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return commentService.page(new Page<>(pageNum, pageSize));
//        }

}

