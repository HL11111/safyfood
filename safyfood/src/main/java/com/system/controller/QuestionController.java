package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IQuestionService;
import com.system.pojo.Question;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ??Ŀ? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/question")
                public class QuestionController {
    
        @Resource
        private IQuestionService questionService;

        @PostMapping
        public Boolean save(@RequestBody Question question) {
            return questionService.saveOrUpdate(question);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return questionService.removeById(id);
        }

        @GetMapping
        public List<Question> findAll() {
            return questionService.list();
        }

        @GetMapping("/{id}")
        public Question findOne(@PathVariable Integer id) {
            return questionService.getById(id);
        }

        @GetMapping("/page")
        public Page<Question> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return questionService.page(new Page<>(pageNum, pageSize));
        }

}

