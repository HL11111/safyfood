package com.system.utils;

import java.util.Random;

public class CodeUtil {


    public static String getRandomCode(Integer length) {
        //for循环 -》 生成1-9之间的数据 -》 拼接
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(10);
            builder.append(num);
        }
        return builder.toString();
    }
}
