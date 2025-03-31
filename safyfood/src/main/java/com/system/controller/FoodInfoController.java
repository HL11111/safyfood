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

import com.system.service.IFoodInfoService;
import com.system.pojo.FoodInfo;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ʳƷ??Ϣ? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/food-info")
public class FoodInfoController {
    
    @Autowired
    private IFoodInfoService foodInfoService;


    @Operation(summary = "商家新增或管理食品信息")
    @PostMapping("/manageFoodInfo")
    public Result manageFoodInfo(@RequestBody Map<String, Object> requestMap) {
        return foodInfoService.manageFoodInfo(requestMap);
    }


    @Operation(summary = "分页查询食品信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return foodInfoService.findAll(pageNum, pageSize, userId);
    }


    @Operation(summary = "查询单一食品信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody FoodInfo food) {
        return foodInfoService.findOne(food);
    }

//        @PostMapping
//        public Boolean save(@RequestBody FoodInfo foodInfo) {
//            return foodInfoService.saveOrUpdate(foodInfo);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return foodInfoService.removeById(id);
//        }
//
//        @GetMapping
//        public List<FoodInfo> findAll() {
//            return foodInfoService.list();
//        }
//
//        @GetMapping("/{id}")
//        public FoodInfo findOne(@PathVariable Integer id) {
//            return foodInfoService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<FoodInfo> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<FoodInfo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return foodInfoService.page(new Page<>(pageNum, pageSize));
//        }

}

