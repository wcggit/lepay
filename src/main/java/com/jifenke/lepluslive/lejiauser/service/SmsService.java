package com.jifenke.lepluslive.lejiauser.service;

import com.jifenke.lepluslive.global.config.Constants;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.SmsClientSend;
import com.jifenke.lepluslive.lejiauser.domain.entities.ValidateCode;
import com.jifenke.lepluslive.lejiauser.repository.ValidateCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * 短信服务
 * Created by zhangwen on 2016/4/26.
 */
@Service
@Transactional(readOnly = true)
public class SmsService {

    private String msg = "\u3010\u4e50\u52a0\u751f\u6d3b\u3011\u9a8c\u8bc1\u7801\u4e3a{0}\uff0c\u6709\u6548\u671f\u4e3a\u0035\u5206\u949f\u3002";

    @Inject
    private ValidateCodeRepository validateCodeRepository;

    @Inject
    private ValidateCodeService validateCodeService;

    /**
     * 发送验证码并保存并创建一个验证码过期的定时任务
     *
     * @param phoneNumber
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveValidateCode(String phoneNumber) {
        String code = MvUtil.getRandomNumber();
        ValidateCode validateCode = new ValidateCode();
        validateCode.setPhoneNumber(phoneNumber);
        validateCode.setCode(code);
        validateCodeRepository.save(validateCode);
        sendValidateCode(phoneNumber, code);

        //生成验证码后,创建quartz任务
        validateCodeService.startValidateCodeJob(validateCode.getId());
    }

    /**
     * 发送短信，此方法建议在job中调用，或者异步调用
     */
    private void sendValidateCode(String phoneNumber, String code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String smsSendUrl = Constants.SMS_SEND_URL;
                String smsUserId = Constants.SMS_USER_ID;
                String smsUserAccount = Constants.SMS_USER_ACCOUNT;
                String smsUserPassword = Constants.SMS_USER_PASSWORD;
                msg = msg.replace("{0}", code);
                SmsClientSend client = new SmsClientSend();
                client.sendSms(smsSendUrl, "send", smsUserId, smsUserAccount,
                        smsUserPassword, phoneNumber, msg);
            }
        }).start();
    }

}
