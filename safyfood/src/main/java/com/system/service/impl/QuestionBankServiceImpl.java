package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.mapper.QuestionMapper;
import com.system.pojo.Question;
import com.system.pojo.QuestionBank;
import com.system.mapper.QuestionBankMapper;
import com.system.pojo.Users;
import com.system.service.IQuestionBankService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ????? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements IQuestionBankService {
    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private IUsersService usersService;

    //题库管理接口
    @Override
    public Result manageQuestionBank(Map<String, Object> requestMap) {
        //获取用户ID
        Integer userId;
        try {
            if (requestMap.get("userId") instanceof Integer) {
                userId = (Integer) requestMap.get("userId");
            } else {
                userId = Integer.parseInt(String.valueOf(requestMap.get("userId")));
            }
        } catch (NumberFormatException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (userId == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //验证用户是否存在
        Users queryUser = new Users();
        queryUser.setUserId(userId);
        Result userResult = usersService.findOne(queryUser);
        if (userResult.getData() == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证用户类型是否为管理
        Map<String, Object> userData = (Map<String, Object>) userResult.getData();
        String userType = (String) userData.get("userType");
        if (!"管理".equals(userType)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        String operationType = String.valueOf(requestMap.get("operationType"));
        if (operationType == null || operationType.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if ("add".equals(operationType)) {
            QuestionBank bank = new QuestionBank();
            bank.setUserId(userId);
            bank.setCreateTime(LocalDateTime.now());
            bank.setUpdateTime(LocalDateTime.now());
            bank.setIsDeleted(0);
            bank.setQuestionCount(0); // 初始题目数量为0
            setBankFields(bank, requestMap);
            int result = questionBankMapper.insert(bank);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("bankId", bank.getBankId());
                data.put("bankType", bank.getBankType());
                data.put("createTime", bank.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer bankId;
            try {
                if (requestMap.get("bankId") instanceof Integer) {
                    bankId = (Integer) requestMap.get("bankId");
                } else {
                    bankId = Integer.parseInt(String.valueOf(requestMap.get("bankId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            QuestionBank existingBank = questionBankMapper.selectById(bankId);
            if (existingBank == null || existingBank.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            setBankFields(existingBank, requestMap);
            existingBank.setUpdateTime(LocalDateTime.now());
            int result = questionBankMapper.updateById(existingBank);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("bankId", existingBank.getBankId());
                data.put("bankType", existingBank.getBankType());
                data.put("updateTime", existingBank.getUpdateTime());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法:设置题库字段
    private void setBankFields(QuestionBank bank, Map<String, Object> requestMap) {
        if (requestMap.containsKey("bankType")) {
            bank.setBankType(String.valueOf(requestMap.get("bankType")));
        }
        if (requestMap.containsKey("bankScore")) {
            bank.setBankScore(new BigDecimal(String.valueOf(requestMap.get("bankScore"))));
        }
        if (requestMap.containsKey("questionCount")) {
            bank.setQuestionCount(Integer.parseInt(String.valueOf(requestMap.get("questionCount"))));
        }
        if (requestMap.containsKey("createTime")) {
            bank.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("updateTime")) {
            bank.setUpdateTime(LocalDateTime.parse(String.valueOf(requestMap.get("updateTime"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            bank.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询题库接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer userId) {
        //验证用户权限
        if (userId != null) {
            Users queryUser = new Users();
            queryUser.setUserId(userId);
            Result userResult = usersService.findOne(queryUser);
            if (userResult.getData() == null) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
            }
            //验证用户类型是否为管理
            Map<String, Object> userData = (Map<String, Object>) userResult.getData();
            String userType = (String) userData.get("userType");
            if (!"管理".equals(userType)) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<QuestionBank> bankPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionBank::getIsDeleted, 0)
                .orderByDesc(QuestionBank::getCreateTime);
        IPage<QuestionBank> page = questionBankMapper.selectPage(bankPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> bankDataList = new ArrayList<>();
        for (QuestionBank bank : page.getRecords()) {
            Map<String, Object> bankDataMap = new HashMap<>();
            Field[] fields = bank.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    bankDataMap.put(field.getName(), field.get(bank));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            bankDataList.add(bankDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", bankDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一题库接口
    @Override
    public Result findOne(QuestionBank bank, Integer userId) {
        //验证用户权限
        if (userId != null) {
            Users queryUser = new Users();
            queryUser.setUserId(userId);
            Result userResult = usersService.findOne(queryUser);
            if (userResult.getData() == null) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
            }
            //验证用户类型是否为管理
            Map<String, Object> userData = (Map<String, Object>) userResult.getData();
            String userType = (String) userData.get("userType");
            if (!"管理".equals(userType)) {
                return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
        }
        if (bank.getBankId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        QuestionBank one = questionBankMapper.selectById(bank.getBankId());
        if (one == null || one.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        Map<String, Object> data = new HashMap<>();
        Field[] fields = one.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                data.put(field.getName(), field.get(one));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Result.ok(data);
    }
}
