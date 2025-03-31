package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IArticleFeedbackService;
import com.system.pojo.ArticleFeedback;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ???·???? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/article-feedback")
                public class ArticleFeedbackController {
    
        @Resource
        private IArticleFeedbackService articleFeedbackService;

        @PostMapping
        public Boolean save(@RequestBody ArticleFeedback articleFeedback) {
            return articleFeedbackService.saveOrUpdate(articleFeedback);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return articleFeedbackService.removeById(id);
        }

        @GetMapping
        public List<ArticleFeedback> findAll() {
            return articleFeedbackService.list();
        }

        @GetMapping("/{id}")
        public ArticleFeedback findOne(@PathVariable Integer id) {
            return articleFeedbackService.getById(id);
        }

        @GetMapping("/page")
        public Page<ArticleFeedback> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<ArticleFeedback> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return articleFeedbackService.page(new Page<>(pageNum, pageSize));
        }

}

