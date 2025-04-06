package com.system.service;

public interface SmsService {


    //发送验证码
    void sendCode(String phone, String code);

}
