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

import com.system.service.IOrdersService;
import com.system.pojo.Orders;

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
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private IOrdersService ordersService;

    @Operation(summary = "订单管理")
    @PostMapping("/manageOrder")
    public Result manageOrder(@RequestBody Map<String, Object> requestMap) {
        return ordersService.manageOrder(requestMap);
    }

    @Operation(summary = "分页查询订单信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return ordersService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一订单信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Orders order) {
        return ordersService.findOne(order);
    }
//        @GetMapping("/userOrders")
//        public Result getOrdersByUserId(@RequestParam Integer userId) {
//            return null;
//        }
//
//        @PostMapping
//        public Boolean save(@RequestBody Orders orders) {
//            return ordersService.saveOrUpdate(orders);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return ordersService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Orders> findAll() {
//            return ordersService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Orders findOne(@PathVariable Integer id) {
//            return ordersService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Orders> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return ordersService.page(new Page<>(pageNum, pageSize));
//        }

}

