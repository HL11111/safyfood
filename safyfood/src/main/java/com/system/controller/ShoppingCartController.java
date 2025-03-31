package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.ServiceInfo;
import com.system.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    
    @Autowired
    private IShoppingCartService shoppingCartService;

    @Operation(summary = "服务信息管理")
    @PostMapping("/manageCart")
    public Result manageCart(@RequestBody Map<String, Object> requestMap) {
        return shoppingCartService.manageCart(requestMap);
    }

    @Operation(summary = "分页查询服务信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return shoppingCartService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一服务信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.findOne(shoppingCart);
    }
//        @PostMapping
//        public Boolean save(@RequestBody ShoppingCart shoppingCart) {
//            return shoppingCartService.saveOrUpdate(shoppingCart);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return shoppingCartService.removeById(id);
//        }
//
//        @GetMapping
//        public List<ShoppingCart> findAll() {
//            return shoppingCartService.list();
//        }
//
//        @GetMapping("/{id}")
//        public ShoppingCart findOne(@PathVariable Integer id) {
//            return shoppingCartService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<ShoppingCart> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return shoppingCartService.page(new Page<>(pageNum, pageSize));
//        }

}

