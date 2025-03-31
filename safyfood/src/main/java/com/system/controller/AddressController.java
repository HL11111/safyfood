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

import com.system.service.IAddressService;
import com.system.pojo.Address;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ??ַ? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private IAddressService addressService;


    @Operation(summary = "新增或管理地址信息")
    @PostMapping("/manageAddress")
    public Result manageAddress(@RequestBody Map<String, Object> requestMap) {
        return addressService.manageAddress(requestMap);
    }


    @Operation(summary = "分页查询地址信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return addressService.findAll(pageNum, pageSize, userId);
    }


    @Operation(summary = "查询单一地址信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Address address) {
        return addressService.findOne(address);
    }
//        @PostMapping
//        public Boolean save(@RequestBody Address address) {
//            return addressService.saveOrUpdate(address);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return addressService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Address> findAll() {
//            return addressService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Address findOne(@PathVariable Integer id) {
//            return addressService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Address> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return addressService.page(new Page<>(pageNum, pageSize));
//        }

}

