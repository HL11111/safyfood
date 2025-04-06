package com.system.exception;

import com.system.utils.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    //匹配原则 谁匹配的最精确谁先获取（先看本身，没有再看父类）

    //表示当前这个handle方法需要捕获并处理的异常类型
    @ExceptionHandler(Exception.class)
    //表示方法的返回值会作为接口发生异常的返回值
    @ResponseBody
    public Result handle(Exception e) {
        e.printStackTrace();
        return Result.fail();

    }

    //表示当前这个handle方法需要捕获并处理的异常类型
    @ExceptionHandler(LeaseException.class)
    //表示方法的返回值会作为接口发生异常的返回值
    @ResponseBody
    public Result handle(LeaseException e) {
        e.printStackTrace();
        return Result.fail(e.getCode(), e.getMessage());

    }
}