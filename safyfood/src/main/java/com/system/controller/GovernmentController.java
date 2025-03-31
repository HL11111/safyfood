package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IGovernmentService;
import com.system.pojo.Government;

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
@RequestMapping("/government")
                public class GovernmentController {
    
        @Resource
        private IGovernmentService governmentService;

        @PostMapping
        public Boolean save(@RequestBody Government government) {
            return governmentService.saveOrUpdate(government);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return governmentService.removeById(id);
        }

        @GetMapping
        public List<Government> findAll() {
            return governmentService.list();
        }

        @GetMapping("/{id}")
        public Government findOne(@PathVariable Integer id) {
            return governmentService.getById(id);
        }

        @GetMapping("/page")
        public Page<Government> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<Government> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return governmentService.page(new Page<>(pageNum, pageSize));
        }

}

