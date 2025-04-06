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
    @Autowired
    private IQuestionBankService questionBankService;

    @Operation(summary = "题库管理")
    @PostMapping("/manageQuestionBank")
    public Result manageQuestionBank(@RequestBody Map<String, Object> requestMap) {
        return questionBankService.manageQuestionBank(requestMap);
    }

    @Operation(summary = "分页查询题库")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return questionBankService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一题库")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody QuestionBank bank,
                          @RequestParam(required = false) Integer userId) {
        return questionBankService.findOne(bank, userId);
    }

//        @Resource
//        private IQuestionBankService questionBankService;
//
//        @PostMapping
//        public Boolean save(@RequestBody QuestionBank questionBank) {
//            return questionBankService.saveOrUpdate(questionBank);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return questionBankService.removeById(id);
//        }
//
//        @GetMapping
//        public List<QuestionBank> findAll() {
//            return questionBankService.list();
//        }
//
//        @GetMapping("/{id}")
//        public QuestionBank findOne(@PathVariable Integer id) {
//            return questionBankService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<QuestionBank> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return questionBankService.page(new Page<>(pageNum, pageSize));
//        }

}

