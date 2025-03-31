package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IInspectionReportService;
import com.system.pojo.InspectionReport;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ?̼Ҽ??ⱨ??? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/inspection-report")
                public class InspectionReportController {
    
        @Resource
        private IInspectionReportService inspectionReportService;

        @PostMapping
        public Boolean save(@RequestBody InspectionReport inspectionReport) {
            return inspectionReportService.saveOrUpdate(inspectionReport);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return inspectionReportService.removeById(id);
        }

        @GetMapping
        public List<InspectionReport> findAll() {
            return inspectionReportService.list();
        }

        @GetMapping("/{id}")
        public InspectionReport findOne(@PathVariable Integer id) {
            return inspectionReportService.getById(id);
        }

        @GetMapping("/page")
        public Page<InspectionReport> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<InspectionReport> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return inspectionReportService.page(new Page<>(pageNum, pageSize));
        }

}

