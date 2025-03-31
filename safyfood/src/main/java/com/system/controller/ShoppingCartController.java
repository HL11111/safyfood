package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IShoppingCartService;
import com.system.pojo.ShoppingCart;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ???ﳵ? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/shopping-cart")
                public class ShoppingCartController {
    
        @Resource
        private IShoppingCartService shoppingCartService;

        @PostMapping
        public Boolean save(@RequestBody ShoppingCart shoppingCart) {
            return shoppingCartService.saveOrUpdate(shoppingCart);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return shoppingCartService.removeById(id);
        }

        @GetMapping
        public List<ShoppingCart> findAll() {
            return shoppingCartService.list();
        }

        @GetMapping("/{id}")
        public ShoppingCart findOne(@PathVariable Integer id) {
            return shoppingCartService.getById(id);
        }

        @GetMapping("/page")
        public Page<ShoppingCart> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return shoppingCartService.page(new Page<>(pageNum, pageSize));
        }

}

