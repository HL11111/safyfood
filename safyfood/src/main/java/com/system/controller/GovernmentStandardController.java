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

import com.system.service.IGovernmentStandardService;
import com.system.pojo.GovernmentStandard;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ??????׼? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/government-standard")
public class GovernmentStandardController {
    @Autowired
    private IGovernmentStandardService governmentStandardService;

    @Operation(summary = "政府标准管理")
    @PostMapping("/manageStandard")
    public Result manageStandard(@RequestBody Map<String, Object> requestMap) {
        return governmentStandardService.manageStandard(requestMap);
    }

    @Operation(summary = "分页查询政府标准")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) String governmentId) {
        return governmentStandardService.findAll(pageNum, pageSize, governmentId);
    }

    @Operation(summary = "查询单一政府标准")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody GovernmentStandard standard) {
        return governmentStandardService.findOne(standard);
    }
//        @Resource
//        private IGovernmentStandardService governmentStandardService;
//
//        @PostMapping
//        public Boolean save(@RequestBody GovernmentStandard governmentStandard) {
//            return governmentStandardService.saveOrUpdate(governmentStandard);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return governmentStandardService.removeById(id);
//        }
//
//        @GetMapping
//        public List<GovernmentStandard> findAll() {
//            return governmentStandardService.list();
//        }
//
//        @GetMapping("/{id}")
//        public GovernmentStandard findOne(@PathVariable Integer id) {
//            return governmentStandardService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<GovernmentStandard> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<GovernmentStandard> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return governmentStandardService.page(new Page<>(pageNum, pageSize));
//        }

}

