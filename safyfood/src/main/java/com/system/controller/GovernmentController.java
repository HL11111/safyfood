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
    @Autowired
    private IGovernmentService governmentService;

    @Operation(summary = "政府机构管理")
    @PostMapping("/manageGovernment")
    public Result manageGovernment(@RequestBody Map<String, Object> requestMap) {
        return governmentService.manageGovernment(requestMap);
    }

    @Operation(summary = "分页查询政府机构")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize) {
        return governmentService.findAll(pageNum, pageSize);
    }

    @Operation(summary = "查询单一政府机构")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Government government) {
        return governmentService.findOne(government);
    }
//        @Resource
//        private IGovernmentService governmentService;
//
//        @PostMapping
//        public Boolean save(@RequestBody Government government) {
//            return governmentService.saveOrUpdate(government);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return governmentService.removeById(id);
//        }
//
//        @GetMapping
//        public List<Government> findAll() {
//            return governmentService.list();
//        }
//
//        @GetMapping("/{id}")
//        public Government findOne(@PathVariable Integer id) {
//            return governmentService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<Government> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Government> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return governmentService.page(new Page<>(pageNum, pageSize));
//        }

}

