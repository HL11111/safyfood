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
    @Autowired
    private IQuestionService questionService;

    @Operation(summary = "题目管理")
    @PostMapping("/manageQuestion")
    public Result manageQuestion(@RequestBody Map<String, Object> requestMap) {
        return questionService.manageQuestion(requestMap);
    }

    @Operation(summary = "分页查询题目")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer bankId) {
        return questionService.findAll(pageNum, pageSize, bankId);
    }

    @Operation(summary = "查询单一题目")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Question question) {
        return questionService.findOne(question);
    }


//        @Resource
//        private IQuestionService questionService;
//
//        @PostMapping
//        public Boolean save(@RequestBody Question question) {
//            return questionService.saveOrUpdate(question);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return questionService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Question> findAll() {
//            return questionService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Question findOne(@PathVariable Integer id) {
//            return questionService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Question> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return questionService.page(new Page<>(pageNum, pageSize));
//        }

}

