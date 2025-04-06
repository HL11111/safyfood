package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.mapper.QuestionBankMapper;
import com.system.pojo.Question;
import com.system.mapper.QuestionMapper;
import com.system.pojo.QuestionBank;
import com.system.pojo.Users;
import com.system.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ??Ŀ? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private IUsersService usersService;

    //题目管理接口(需要管理权限,题目表变化,同步更改题库表内容)
    @Override
    public Result manageQuestion(Map<String, Object> requestMap) {
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
            Question question = new Question();
            question.setCreateTime(LocalDateTime.now());
            question.setUpdateTime(LocalDateTime.now());
            question.setIsDeleted(0);
            question.setUsesTimes(0);
            setQuestionFields(question, requestMap);
            //验证是否关联了题库
            if (question.getBankId() == null) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }
            //验证题库是否存在
            QuestionBank questionBank = questionBankMapper.selectById(question.getBankId());
            if (questionBank == null || questionBank.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            int result = questionMapper.insert(question);
            if (result > 0) {
                // 更新题库的题目数量
                questionBank.setQuestionCount(questionBank.getQuestionCount() + 1);
                questionBank.setUpdateTime(LocalDateTime.now());
                questionBankMapper.updateById(questionBank);

                Map<String, Object> data = new HashMap<>();
                data.put("questionId", question.getQuestionId());
                data.put("bankId", question.getBankId());
                data.put("questionType", question.getQuestionType());
                data.put("createTime", question.getCreateTime());
                return Result.ok(data);
            }
        } else if ("update".equals(operationType)) {
            Integer questionId;
            try {
                if (requestMap.get("questionId") instanceof Integer) {
                    questionId = (Integer) requestMap.get("questionId");
                } else {
                    questionId = Integer.parseInt(String.valueOf(requestMap.get("questionId")));
                }
            } catch (NumberFormatException e) {
                return Result.build(null, ResultCodeEnum.PARAM_ERROR);
            }

            Question existingQuestion = questionMapper.selectById(questionId);
            if (existingQuestion == null || existingQuestion.getIsDeleted() == 1) {
                return Result.build(null, ResultCodeEnum.DATA_ERROR);
            }
            //保存原题库ID
            Integer oldBankId = existingQuestion.getBankId();
            setQuestionFields(existingQuestion, requestMap);
            existingQuestion.setUpdateTime(LocalDateTime.now());
            int result = questionMapper.updateById(existingQuestion);
            if (result > 0) {
                //如果题库ID发生变化，需要更新旧题库和新题库的题目数量
                if (oldBankId != null && !oldBankId.equals(existingQuestion.getBankId())) {
                    //更新旧题库
                    QuestionBank oldBank = questionBankMapper.selectById(oldBankId);
                    if (oldBank != null && oldBank.getQuestionCount() > 0) {
                        oldBank.setQuestionCount(oldBank.getQuestionCount() - 1);
                        oldBank.setUpdateTime(LocalDateTime.now());
                        questionBankMapper.updateById(oldBank);
                    }
                    //更新新题库
                    QuestionBank newBank = questionBankMapper.selectById(existingQuestion.getBankId());
                    if (newBank != null) {
                        newBank.setQuestionCount(newBank.getQuestionCount() + 1);
                        newBank.setUpdateTime(LocalDateTime.now());
                        questionBankMapper.updateById(newBank);
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("questionId", existingQuestion.getQuestionId());
                data.put("bankId", existingQuestion.getBankId());
                data.put("updateTime", existingQuestion.getUpdateTime());
                return Result.ok(data);
            }
        }
        return Result.build(null, ResultCodeEnum.FAIL);
    }
    //辅助方法: 设置题目字段
    private void setQuestionFields(Question question, Map<String, Object> requestMap) {
        if (requestMap.containsKey("bankId")) {
            question.setBankId(Integer.parseInt(String.valueOf(requestMap.get("bankId"))));
        }
        if (requestMap.containsKey("questionType")) {
            question.setQuestionType(Integer.parseInt(String.valueOf(requestMap.get("questionType"))));
        }
        if (requestMap.containsKey("questionTags")) {
            question.setQuestionTags(String.valueOf(requestMap.get("questionTags")));
        }
        if (requestMap.containsKey("questionLevel")) {
            question.setQuestionLevel(Integer.parseInt(String.valueOf(requestMap.get("questionLevel"))));
        }
        if (requestMap.containsKey("questionContent")) {
            question.setQuestionContent(String.valueOf(requestMap.get("questionContent")));
        }
        if (requestMap.containsKey("questionOrigin")) {
            question.setQuestionOrigin(String.valueOf(requestMap.get("questionOrigin")));
        }
        if (requestMap.containsKey("optionA")) {
            question.setOptionA(String.valueOf(requestMap.get("optionA")));
        }
        if (requestMap.containsKey("optionB")) {
            question.setOptionB(String.valueOf(requestMap.get("optionB")));
        }
        if (requestMap.containsKey("optionC")) {
            question.setOptionC(String.valueOf(requestMap.get("optionC")));
        }
        if (requestMap.containsKey("optionD")) {
            question.setOptionD(String.valueOf(requestMap.get("optionD")));
        }
        if (requestMap.containsKey("answerContent")) {
            question.setAnswerContent(String.valueOf(requestMap.get("answerContent")));
        }
        if (requestMap.containsKey("answerAnalysis")) {
            question.setAnswerAnalysis(String.valueOf(requestMap.get("answerAnalysis")));
        }
        if (requestMap.containsKey("usesTimes")) {
            question.setUsesTimes(Integer.parseInt(String.valueOf(requestMap.get("usesTimes"))));
        }
        if (requestMap.containsKey("createTime")) {
            question.setCreateTime(LocalDateTime.parse(String.valueOf(requestMap.get("createTime"))));
        }
        if (requestMap.containsKey("updateTime")) {
            question.setUpdateTime(LocalDateTime.parse(String.valueOf(requestMap.get("updateTime"))));
        }
        if (requestMap.containsKey("isDeleted")) {
            question.setIsDeleted(Integer.parseInt(String.valueOf(requestMap.get("isDeleted"))));
        }
    }

    //分页查询题目接口
    @Override
    public Result findAll(Integer pageNum, Integer pageSize, Integer bankId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Question> questionPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getIsDeleted, 0)
                .orderByDesc(Question::getCreateTime);
        //根据题库ID查询
        if (bankId != null) {
            queryWrapper.eq(Question::getBankId, bankId);
        }
        IPage<Question> page = questionMapper.selectPage(questionPage, queryWrapper);
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        List<Map<String, Object>> questionDataList = new ArrayList<>();
        for (Question question : page.getRecords()) {
            Map<String, Object> questionDataMap = new HashMap<>();
            Field[] fields = question.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    questionDataMap.put(field.getName(), field.get(question));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            questionDataList.add(questionDataMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", questionDataList);
        data.put("total", page.getTotal());
        data.put("pages", page.getPages());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        return Result.ok(data);
    }

    //查询单一题目接口
    @Override
    public Result findOne(Question question) {
        if (question.getQuestionId() == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        Question one = questionMapper.selectById(question.getQuestionId());
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
