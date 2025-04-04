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

import com.system.service.IFoodInspectionReportService;
import com.system.pojo.FoodInspectionReport;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ʳƷ???ⱨ??? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/food-inspection-report")
public class FoodInspectionReportController {
    @Autowired
    private IFoodInspectionReportService foodInspectionReportService;

    @Operation(summary = "食品检查报告管理")
    @PostMapping("/manageReport")
    public Result manageReport(@RequestBody Map<String, Object> requestMap) {
        return foodInspectionReportService.manageReport(requestMap);
    }

    @Operation(summary = "分页查询食品检查报告")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) String foodId) {
        return foodInspectionReportService.findAll(pageNum, pageSize, foodId);
    }

    @Operation(summary = "查询单一食品检查报告")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody FoodInspectionReport report) {
        return foodInspectionReportService.findOne(report);
    }
//        @Resource
//        private IFoodInspectionReportService footInspectionReportService;
//
//        @PostMapping
//        public Boolean save(@RequestBody FoodInspectionReport footInspectionReport) {
//            return footInspectionReportService.saveOrUpdate(footInspectionReport);
//        }
//
//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return footInspectionReportService.removeById(id);
//        }
//
//        @GetMapping
//        public List<FoodInspectionReport> findAll() {
//            return footInspectionReportService.list();
//        }
//
//        @GetMapping("/{id}")
//        public FoodInspectionReport findOne(@PathVariable Integer id) {
//            return footInspectionReportService.getById(id);
//        }
//
//        @GetMapping("/page")
//        public Page<FoodInspectionReport> findPage(@RequestParam Integer pageNum,
//                                                   @RequestParam Integer pageSize) {
//            QueryWrapper<FoodInspectionReport> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return footInspectionReportService.page(new Page<>(pageNum, pageSize));
//        }

}

