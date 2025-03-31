package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
    
        @Resource
        private ICommentService commentService;

        @PostMapping
        public Boolean save(@RequestBody Comment comment) {
            return commentService.saveOrUpdate(comment);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return commentService.removeById(id);
        }

        @GetMapping
        public List<Comment> findAll() {
            return commentService.list();
        }

        @GetMapping("/{id}")
        public Comment findOne(@PathVariable Integer id) {
            return commentService.getById(id);
        }

        @GetMapping("/page")
        public Page<Comment> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return commentService.page(new Page<>(pageNum, pageSize));
        }

}

