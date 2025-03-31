package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
    
        @Resource
        private IServiceInfoService serviceInfoService;

        @PostMapping
        public Boolean save(@RequestBody ServiceInfo serviceInfo) {
            return serviceInfoService.saveOrUpdate(serviceInfo);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return serviceInfoService.removeById(id);
        }

        @GetMapping
        public List<ServiceInfo> findAll() {
            return serviceInfoService.list();
        }

        @GetMapping("/{id}")
        public ServiceInfo findOne(@PathVariable Integer id) {
            return serviceInfoService.getById(id);
        }

        @GetMapping("/page")
        public Page<ServiceInfo> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<ServiceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return serviceInfoService.page(new Page<>(pageNum, pageSize));
        }

}

