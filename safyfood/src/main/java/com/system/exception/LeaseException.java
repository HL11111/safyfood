package com.system.exception;



import com.system.utils.ResultCodeEnum;
import lombok.Data;

/**
 * @Author mikasan
 * title
 */
@Data
public class LeaseException extends RuntimeException {
    private Integer code;

    //构造器
    public LeaseException(Integer code, String message) {
        super(message);//调用父类的属性
        this.code = code;
    }

    //构造器
    public LeaseException(com.system.utils.ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }


}
