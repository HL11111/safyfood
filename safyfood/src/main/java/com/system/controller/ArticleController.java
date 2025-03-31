package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
    
        @Resource
        private IArticleService articleService;

        @PostMapping
        public Boolean save(@RequestBody Article article) {
            return articleService.saveOrUpdate(article);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return articleService.removeById(id);
        }

        @GetMapping
        public List<Article> findAll() {
            return articleService.list();
        }

        @GetMapping("/{id}")
        public Article findOne(@PathVariable Integer id) {
            return articleService.getById(id);
        }

        @GetMapping("/page")
        public Page<Article> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return articleService.page(new Page<>(pageNum, pageSize));
        }

}

