package com.system.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Users;
import com.system.mapper.UsersMapper;
import com.system.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.utils.*;
import io.jsonwebtoken.lang.Classes;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * ?û?? 服务实现类
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private JwtHelper jwtHelper;



    @Override
    public boolean MerchantExists(Integer userId) {
        //商家是否存在
        //有数据 -> true
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUserId, userId);
        queryWrapper.eq(Users::getIsDeleted, 0);
        queryWrapper.eq(Users::getUserType,"商家");
        Long count = usersMapper.selectCount(queryWrapper);

        return count > 0;
    }

    //用户登录接口
    @Override
    public Result login(Users user) {
        Users loginUsers = usersMapper.selectByUserName(user.getUserName());
        if (loginUsers == null || loginUsers.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //System.out.println(MD5Util.encrypt(user.getPassword()));
        //对比密码
        if (!StringUtils.isEmpty(user.getPassword())
                && MD5Util.encrypt(user.getPassword()).equals(loginUsers.getPassword())) {
            //登录成功
            //根据用户id生成 token
            String token = jwtHelper.createToken(Long.valueOf(loginUsers.getUserId()));
            //将token封装到result返回
            Map data = new HashMap();
            data.put("token", token);
            data.put("userName", loginUsers.getUserName());
            if (loginUsers.getUserName().equals("zs") &&
                    usersMapper.selectByUserName("zs").getPassword()
                            .equals(loginUsers.getPassword())) {
                System.out.println("成功进入二级密码校验");
                if (loginUsers.getSecondPassword() != null &&
                        loginUsers.getSecondPassword()
                                .equals(usersMapper.selectByUserName("zs").getSecondPassword())) {
                    data.put("adminLevel",1);
                }
                else {
                    return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
                }
            }
//            String url = String.format("http://localhost:8080/%s",role);
//            data.put("url", url);
            data.put("userId", loginUsers.getUserId());
            data.put("userType", loginUsers.getUserType());
            data.put("IsDeleted", loginUsers.getIsDeleted());
            return Result.ok(data);
        }
        return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
    }


    //用户注册接口
    // 新增：可以将验证码存储到缓存中，这里简单示例用一个临时Map存储（实际应使用专业缓存如Redis）
    // 假设使用一个全局的静态Map来模拟缓存
    // 将验证码缓存定义为类的静态成员变量
//    private static final Map<String, String> verificationCodeCache = new HashMap<>();
    @Override
    public Result regist(Users user) {
        // 新增：获取用户手机号，假设Users实体类中有getPhone方法获取手机号
//        String phoneNumber = user.getPhone();
//        // 新增：发送短信验证码并获取验证码内容
//        String verificationCode = TencentSmsUtil.sendVerificationCode(phoneNumber);
//        if (verificationCode == null) {
//            return Result.build(null, ResultCodeEnum.SMS_SEND_ERROR); // 假设ResultCodeEnum中新增了短信发送失败的枚举项
//        }
//        verificationCodeCache.put(phoneNumber, verificationCode);

        Long count = usersMapper.selectCountByUserName(user.getUserName());
//        System.out.println(count);
        if (count > 0) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_EXIST_ERROR);
        }
        Map data = new HashMap();
        data.put("userName", user.getUserName());
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        String type = null;
        LocalDateTime now = LocalDateTime.now();
        user.setRegisterTime(now);
        if (user.getUserType() != null) {
            if (user.getUserType().equals("用户")) {
                type = "用户";
            }
            else if (user.getUserType().equals("商家")) {
                type = "商家";
            }
            else {
                //用错误的用户类型会注册失败
                return Result.build(null,ResultCodeEnum.PARAM_ERROR);
            }
        }
        int rows = usersMapper.insert(user);
        data.put("registTime", now);
        data.put("userType", type);
        data.put("userId", user.getUserId());
        System.out.println("rows = " + rows);
        return Result.ok(data);
    }


    //用户删除接口
    @Override
    public Result delete(Users user) {
        Users deleteUser = usersMapper.selectByUserId(user.getUserId());//null?
        Long count = usersMapper.selectCountByUserId(String.valueOf(user.getUserId()));
        if (count <= 0) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //逻辑删除data中呈现
        deleteUser.setIsDeleted(1);
        LocalDateTime now = LocalDateTime.now();
        deleteUser.setUpdateTime(now);
        //逻辑删除持久化 --> is_deleted = 1
        usersMapper.isDeletedByUserId(deleteUser.getUserId());
        usersMapper.updateById(deleteUser);
        Map data = new HashMap();
        data.put("userId", deleteUser.getUserId());
        data.put("userName", deleteUser.getUserName());
        data.put("updateTime",now);
        String type = null;
        if (deleteUser.getUserType() != null) {
            if (deleteUser.getUserType().equals("用户")) {
                type = "用户";
            }
            else if (deleteUser.getUserType().equals("商家")) {
                type = "商家";
            }
            else {
                //用错误的用户类型会注册失败
                return Result.build(null,ResultCodeEnum.PARAM_ERROR);
            }
        }
        data.put("userType", type);
        data.put("userStatus", "已删除");
        data.put("userIsDeleted", deleteUser.getIsDeleted());
        return Result.ok(data);
    }


    //查询单一用户接口 (反射爽!!!)
    @Override
    public Result findOne(Users user) {
        Users One = usersMapper.selectByUserId(user.getUserId());//null?
        Long count = usersMapper.selectCountByUserId(String.valueOf(user.getUserId()));
        if (count <= 0) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //构建返回数据
        Map<String, Object> data = new HashMap<>();
        //使用反射获取实体类的所有字段
        Field[] fields = One.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                //将字段名作为键，字段值作为值放入data中
                data.put(field.getName(), field.get(One));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Result.ok(data);
    }


    //分页查询用户接口 (查询条件为is_deleted = 0)
    @Override
    public Result findAll(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Users> usersPage = new Page<>(pageNum, pageSize);
        IPage<Users> page = usersMapper.selectPage(usersPage, null);
//        page.getRecords()为空!!!?
        if (page.getRecords().isEmpty()) {
            return Result.build(null, ResultCodeEnum.NO_USERS_FOUND);
        }
        List<Map<String, Object>> userDataList = new ArrayList<>();
        for (Users user : page.getRecords()) {
            Map<String, Object> userDataMap = new HashMap<>();
            Field[] fields = user.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    userDataMap.put(field.getName(), field.get(user));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            userDataList.add(userDataMap);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("data", userDataList);
        data.put("totalPages", page.getPages());
        data.put("totalElements", page.getTotal());

        return Result.ok(data);
    }


    //辅助方法：根据字段类型转换值
    private Object convertValueToFieldType(Object value, Class<?> fieldType) {
        if (value == null) {
            return null;
        }
        try {
            //字符串类型
            if (fieldType == String.class) {
                return String.valueOf(value);
            }
            //整数类型
            if (fieldType == Integer.class || fieldType == int.class) {
                if (value instanceof Integer) {
                    return value;
                }
                return Integer.parseInt(String.valueOf(value));
            }
            //布尔类型
            if (fieldType == Boolean.class || fieldType == boolean.class) {
                if (value instanceof Boolean) {
                    return value;
                }
                return Boolean.parseBoolean(String.valueOf(value));
            }
            //日期类型
            if (fieldType == LocalDateTime.class) {
                if (value instanceof LocalDateTime) {
                    return value;
                }
                return LocalDateTime.parse(String.valueOf(value));
            }
            if (fieldType == LocalDate.class) {
                if (value instanceof LocalDate) {
                    return value;
                }
                return LocalDate.parse(String.valueOf(value));
            }
            //如果是其他类型，直接返回原值
            return value;
        } catch (Exception e) {
            //转换失败时返回null
            return null;
        }
    }
    //管理自由修改用户任意字段信息
    @Override
    public Result updateUserField(Map<String, Object> requestMap) {
        //获取管理员信息(通过用户名)
        String adminUserName = String.valueOf(requestMap.get("adminUserName"));
        if (adminUserName == null || adminUserName.isEmpty()) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //查询管理员用户
        Users adminUser = usersMapper.selectByUserName(adminUserName);
        if (adminUser == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证管理员权限
        if (!"管理".equals(adminUser.getUserType()) || adminUser.getSecondPassword() == null || adminUser.getSecondPassword().isEmpty()) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        //验证二级密码
        String providedSecondPassword = String.valueOf(requestMap.get("secondPassword"));
        if (providedSecondPassword == null ||
                !adminUser.getSecondPassword().equals(MD5Util.encrypt(providedSecondPassword))) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        //获取要修改的用户ID
        Integer targetUserId;
        try {
            if (requestMap.get("targetUserId") instanceof Integer) {
                targetUserId = (Integer) requestMap.get("targetUserId");
            } else {
                targetUserId = Integer.parseInt(String.valueOf(requestMap.get("targetUserId")));
            }
        } catch (NumberFormatException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if (targetUserId == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //查询目标用户
        Users targetUser = usersMapper.selectByUserId(targetUserId);
        if (targetUser == null) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userName", targetUser.getUserName());
        //检查目标用户是否也是管理员
        if ("管理".equals(targetUser.getUserType()) && targetUser.getSecondPassword() != null && !targetUser.getSecondPassword().isEmpty()) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        //获取要修改的字段和新值
        String fieldName = String.valueOf(requestMap.get("fieldName"));
        Object newValue = requestMap.get("newValue");
        if (fieldName == null || fieldName.equals("null") || newValue == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        try {
            //使用反射获取字段
            Field field = Users.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            //获取修改前的值
            Object oldValue = field.get(targetUser);
            //获取字段类型
            Class<?> fieldType = field.getType();
            //根据字段类型转换新值
            Object convertedValue = convertValueToFieldType(newValue, fieldType);
            //设置新值
            field.set(targetUser, convertedValue);
            //更新用户信息
            LocalDateTime now = LocalDateTime.now();
            targetUser.setUpdateTime(now);
            int result = usersMapper.updateById(targetUser);
            if (result > 0) {
                data.put("userId", targetUser.getUserId());
                data.put("updatedField", fieldName);
                data.put("newValue", newValue);
                data.put("oldValue", oldValue);
                data.put("updateTime",now);
                return Result.ok(data);
            } else {
                return Result.build(null, ResultCodeEnum.FAIL);
            }

        } catch (NoSuchFieldException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        } catch (IllegalAccessException e) {
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        }
    }


    //用户自我信息修改接口
    //用户个人信息修改,商家自身店铺信息修改
    @Override
    public Result updateSelfInfo(Map<String, Object> requestMap) {
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
        //查询用户
        Users user = usersMapper.selectByUserId(userId);
        if (user == null || user.getIsDeleted() == 1) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证用户类型，只有"商家"和"用户"可以使用此接口
        String userType = user.getUserType();
        if (!"商家".equals(userType) && !"用户".equals(userType)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }
        //获取要修改的字段和新值
        String fieldName = String.valueOf(requestMap.get("fieldName"));
        Object newValue = requestMap.get("newValue");
        //若是密码,则新值也加密
        if (fieldName.equals("password")) {
            newValue = MD5Util.encrypt(String.valueOf(requestMap.get("newValue")));
        }
        if (fieldName == null || fieldName.equals("null") || newValue == null) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //检查是否是禁止修改的字段
        if ("userId".equals(fieldName) || "isDeleted".equals(fieldName) || "userStatus".equals(fieldName)
        || "workerIcon".equals(fieldName) || "greenIcon".equals(fieldName) || "isGreenCertified".equals(fieldName)
        || "adminLevel".equals(fieldName) || "certificationDate".equals(fieldName)) {
            return Result.build(null, ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }

        try {
            //使用反射获取字段
            Field field = Users.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            //获取修改前的值
            Object oldValue = field.get(user);
            //获取字段类型
            Class<?> fieldType = field.getType();
            //根据字段类型转换新值
            Object convertedValue = convertValueToFieldType(newValue, fieldType);
            //设置新值
            field.set(user, convertedValue);
            //更新用户信息
            LocalDateTime now = LocalDateTime.now();
            user.setUpdateTime(now);
            int result = usersMapper.updateById(user);
            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getUserId());
                data.put("userName", user.getUserName());
                data.put("updatedField", fieldName);
                data.put("newValue", newValue);
                data.put("oldValue", oldValue);
                data.put("updateTime",now);
                return Result.ok(data);
            } else {
                return Result.build(null, ResultCodeEnum.FAIL);
            }

        } catch (NoSuchFieldException e) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        } catch (IllegalAccessException e) {
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        }
    }

    //

}

