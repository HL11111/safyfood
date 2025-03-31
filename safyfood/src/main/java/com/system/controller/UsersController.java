package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.utils.Result;
import io.jsonwebtoken.lang.Classes;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.system.service.IUsersService;
import com.system.pojo.Users;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ?û?? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private IUsersService usersService;

    @Operation(summary ="根据用户信息获取登录响应")
    @PostMapping("/login")
    public Result login(@RequestBody Users user) {
        System.out.println(user.getUserName());
        return usersService.login(user);
    }

    @Operation(summary = "根据用户信息注册")
    @PostMapping("/regist")
    public Result regist(@RequestBody Users user) {
        return usersService.regist(user);
    }

    @Operation(summary = "根据用户Id删除用户")
    @PostMapping("/delete")
    public Result delete(@RequestBody Users user) {
        return usersService.delete(user);
    }

    @Operation(summary = "根据用户Id查询用户")
    @GetMapping("/findOne")
    public Result findOne(@RequestBody Users user) {
        return usersService.findOne(user);
    }

    @Operation(summary = "查询所有用户(分页)")
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize) {
        return usersService.findAll(pageNum, pageSize);
    }

    @Operation(summary = "管理修改用户信息")
    @PostMapping("/updateUserField")
    public Result updateUserField(@RequestBody Map<String, Object> requestMap) {
        return usersService.updateUserField(requestMap);
    }

    @Operation(summary = "用户修改自身信息")
    @PostMapping("/updateSelfInfo")
    public Result updateSelfInfo(@RequestBody Map<String, Object> requestMap) {
        return usersService.updateSelfInfo(requestMap);
    }

//        增加用户
//        @PostMapping
//        public Boolean save(@RequestBody Users users) {
//            return usersService.saveOrUpdate(users);
//        }

//        @DeleteMapping("/{id}")
//        public Boolean delete(@PathVariable Integer id) {
//            return usersService.removeById(id);
//        }

//        @GetMapping("/findAll")
//        public List<Users> findAll() {
//            return usersService.list();
//        }
////
////        @GetMapping("/{id}")
////        public Users findOne(@PathVariable Integer id) {
////            return usersService.getById(id);
////        }
//
//        @GetMapping("/page")
//        public Page<Users> findPage(@RequestParam Integer pageNum,
//                                        @RequestParam Integer pageSize) {
//            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
//            queryWrapper.orderByDesc("id");
//            return usersService.page(new Page<>(pageNum, pageSize));
//        }

}

