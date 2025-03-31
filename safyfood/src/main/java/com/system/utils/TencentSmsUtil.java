package com.system.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;

import java.util.Random;

public class TencentSmsUtil {
    // 这里替换为你在腾讯云控制台申请的SecretId
    private static final String SECRET_ID = "your_secret_id";
    // 这里替换为你在腾讯云控制台申请的SecretKey
    private static final String SECRET_KEY = "your_secret_key";
    // 这里替换为你在腾讯云控制台申请的短信签名
    private static final String SIGN_NAME = "your_sign_name";
    // 这里替换为你在腾讯云控制台申请的验证码短信模板ID
    private static final String TEMPLATE_ID = "your_template_id";

    public static String sendVerificationCode(String phoneNumber) {
        // 生成6位随机验证码
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        try {
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            req.setSign(SIGN_NAME);
            req.setTemplateID(TEMPLATE_ID);
            String[] phoneNumbers = {phoneNumber};
            req.setPhoneNumberSet(phoneNumbers);
            String[] templateParams = {verificationCode};
            req.setTemplateParamSet(templateParams);

            SendSmsResponse resp = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(resp));
            return verificationCode;
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return null;
        }
    }
}

