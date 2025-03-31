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

import com.system.service.IArticleService;
import com.system.pojo.Article;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ???± 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private IArticleService articleService;

    @Operation(summary = "文章管理")
    @PostMapping("/manageArticle")
    public Result manageArticle(@RequestBody Map<String, Object> requestMap) {
        return articleService.manageArticle(requestMap);
    }

    @Operation(summary = "分页查询文章信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return articleService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一文章信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Article article) {
        return articleService.findOne(article);
    }
}
//        @PostMapping
//        public Boolean save(@RequestBody Article article) {
//            return articleService.saveOrUpdate(article);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return articleService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Article> findAll() {
//            return articleService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Article findOne(@PathVariable Integer id) {
//            return articleService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Article> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return articleService.page(new Page<>(pageNum, pageSize));
//        }



