package com.system.Scheduled;


import com.system.pojo.Users;
import com.system.service.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CertificateExpiryCheckService {

    @Autowired
    private IUsersService usersService;

    // 每天凌晨1点检查一次
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkExpiredCertificates() {
        List<Users> certifiedMerchants = usersService.list()
                .stream()
                .filter(Users::getIsGreenCertified)
                .collect(Collectors.toList());

        for (Users merchant : certifiedMerchants) {
            if (merchant.getCertificationDate() != null) {
                boolean isExpired = isCertificateExpired(merchant.getCertificationDate(), 36);
                if (isExpired) {
                    merchant.setIsGreenCertified(false);
                    merchant.setCertificationDate("");
                    //merchant.setSafetyLevel("");
                    usersService.updateMerchant(merchant);
                    log.info("商家 {} 的绿色认证已过期，状态已更新", merchant.getUserId());
                }
            }
        }
    }

    private boolean isCertificateExpired(String date, int months) {
        try {
            LocalDate certDate = LocalDate.parse(date);
            return LocalDate.now().isAfter(certDate.plusMonths(months));
        } catch (Exception e) {
            log.error("日期解析失败: {}", date);
            return false;
        }
    }
}