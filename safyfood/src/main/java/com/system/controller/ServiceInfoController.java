package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Orders;
import com.system.service.IOrdersService;
import com.system.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.system.service.IServiceInfoService;
import com.system.pojo.ServiceInfo;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ??????Ϣ? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/service-info")
public class ServiceInfoController {
    @Autowired
    private IServiceInfoService serviceInfoService;

    @Operation(summary = "服务信息管理")
    @PostMapping("/manageService")
    public Result manageService(@RequestBody Map<String, Object> requestMap) {
        return serviceInfoService.manageService(requestMap);
    }

    @Operation(summary = "分页查询服务信息")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) Integer userId) {
        return serviceInfoService.findAll(pageNum, pageSize, userId);
    }

    @Operation(summary = "查询单一服务信息")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody ServiceInfo serviceInfo) {
        return serviceInfoService.findOne(serviceInfo);
    }
//        @PostMapping
//        public Boolean save(@RequestBody ServiceInfo serviceInfo) {
//            return serviceInfoService.saveOrUpdate(serviceInfo);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return serviceInfoService.removeById(id);
//        }
//
//        @GetMapping
//        public List<ServiceInfo> findAll() {
//            return serviceInfoService.list();
//        }
//
//        @GetMapping("/{id}")
//        public ServiceInfo findOne(@PathVariable Integer id) {
//            return serviceInfoService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<ServiceInfo> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<ServiceInfo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return serviceInfoService.page(new Page<>(pageNum, pageSize));
//        }

}

