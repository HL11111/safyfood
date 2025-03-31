package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.system.service.IFootInspectionReportService;
import com.system.pojo.FootInspectionReport;

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
@RequestMapping("/foot-inspection-report")
                public class FootInspectionReportController {
    
        @Resource
        private IFootInspectionReportService footInspectionReportService;

        @PostMapping
        public Boolean save(@RequestBody FootInspectionReport footInspectionReport) {
            return footInspectionReportService.saveOrUpdate(footInspectionReport);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return footInspectionReportService.removeById(id);
        }

        @GetMapping
        public List<FootInspectionReport> findAll() {
            return footInspectionReportService.list();
        }

        @GetMapping("/{id}")
        public FootInspectionReport findOne(@PathVariable Integer id) {
            return footInspectionReportService.getById(id);
        }

        @GetMapping("/page")
        public Page<FootInspectionReport> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<FootInspectionReport> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return footInspectionReportService.page(new Page<>(pageNum, pageSize));
        }

}

