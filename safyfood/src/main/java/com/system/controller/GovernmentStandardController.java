package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
@Tag(name ="政府标准信息管理")
@RequestMapping("/government-standard")
public class GovernmentStandardController {

    @Autowired
    private IGovernmentStandardService governmentStandardService;

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询某政府标准信息")
    public GovernmentStandard findOne(@PathVariable String id) {
        LambdaQueryWrapper<GovernmentStandard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GovernmentStandard::getStandardId,id);

        return governmentStandardService.getOne(queryWrapper);
    }

    @Operation(summary = "分页查询政府标准")
    @GetMapping("/page")
    public Page<GovernmentStandard> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<GovernmentStandard> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return governmentStandardService.page(new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "查询所有政府标准")
    @GetMapping
    public List<GovernmentStandard> findAll() {
        return governmentStandardService.list();
    }

    /*
            @Resource
            private IGovernmentStandardService governmentStandardService;

            @PostMapping
            public Boolean save(@RequestBody GovernmentStandard governmentStandard) {
                return governmentStandardService.saveOrUpdate(governmentStandard);
            }

            @DeleteMapping("/{id}")
            public Boolean delete(@PathVariable Integer id) {
                return governmentStandardService.removeById(id);
            }
    */
}

