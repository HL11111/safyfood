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
    @Autowired
    private IArticleFeedbackService articleFeedbackService;

    @Operation(summary = "文章反馈管理")
    @PostMapping("/manageFeedback")
    public Result manageFeedback(@RequestBody Map<String, Object> requestMap) {
        return articleFeedbackService.manageFeedback(requestMap);
    }

    @Operation(summary = "分页查询文章反馈")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return articleFeedbackService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一文章反馈")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody ArticleFeedback feedback) {
        return articleFeedbackService.findOne(feedback);
    }
//        @Resource
//        private IArticleFeedbackService articleFeedbackService;
//
//        @PostMapping
//        public Boolean save(@RequestBody ArticleFeedback articleFeedback) {
//            return articleFeedbackService.saveOrUpdate(articleFeedback);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return articleFeedbackService.removeById(id);
//        }
//
//        @GetMapping
//        public List<ArticleFeedback> findAll() {
//            return articleFeedbackService.list();
//        }
//
//        @GetMapping("/{id}")
//        public ArticleFeedback findOne(@PathVariable Integer id) {
//            return articleFeedbackService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<ArticleFeedback> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<ArticleFeedback> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return articleFeedbackService.page(new Page<>(pageNum, pageSize));
//        }

}

