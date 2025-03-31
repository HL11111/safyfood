package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IQuestionBankService;
import com.system.pojo.QuestionBank;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ????? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/question-bank")
                public class QuestionBankController {
    
        @Resource
        private IQuestionBankService questionBankService;

        @PostMapping
        public Boolean save(@RequestBody QuestionBank questionBank) {
            return questionBankService.saveOrUpdate(questionBank);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return questionBankService.removeById(id);
        }

        @GetMapping
        public List<QuestionBank> findAll() {
            return questionBankService.list();
        }

        @GetMapping("/{id}")
        public QuestionBank findOne(@PathVariable Integer id) {
            return questionBankService.getById(id);
        }

        @GetMapping("/page")
        public Page<QuestionBank> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return questionBankService.page(new Page<>(pageNum, pageSize));
        }

}

