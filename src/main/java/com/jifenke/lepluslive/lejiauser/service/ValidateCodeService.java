//package com.jifenke.lepluslive.lejiauser.service;
//
//import com.jifenke.lepluslive.global.config.Constants;
////import com.jifenke.lepluslive.job.ValidateCodeJob;
//import com.jifenke.lepluslive.lejiauser.domain.entities.ValidateCode;
//import com.jifenke.lepluslive.lejiauser.repository.ValidateCodeRepository;
//import org.quartz.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.inject.Inject;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Created by zhangwen on 2016/4/25.
// */
//@Service
//@Transactional(readOnly = true)
//public class ValidateCodeService {
//
//    @Inject
//    private ValidateCodeRepository validateCodeRepository;
//
//    @Inject
//    private Scheduler scheduler;
//
//    private static String jobGroupName = "VALIDATECODE_JOBGROUP_NAME";
//    private static String triggerGroupName = "VALIDATECODE_TRIGGERGROUP_NAME";
//
//    /**
//     * 判断验证码是否正确
//     *
//     * @param phoneNumber 手机号码
//     * @param code        验证码
//     * @return true=验证码正确  false=验证码错误
//     */
//    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
//    public Boolean findByPhoneNumberAndCode(String phoneNumber, String code) {
//        ValidateCode validateCode = validateCodeRepository.findByPhoneNumberAndCodeAndStatus(phoneNumber, code, 0);
//        if (validateCode != null) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 定时任务
//     * 修改验证码状态失之失效
//     *
//     * @param codeId 验证码id
//     */
//    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
//    public void changeValideteCodeState(Long codeId) {
//        ValidateCode validateCode = validateCodeRepository.findOne(codeId);
//        validateCode.setStatus(1);
//        validateCodeRepository.save(validateCode);
//    }
//
//    /**
//     * 开启线程创建验证码定时过期任务
//     */
//    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
//    public void startValidateCodeJob(Long codeId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                try {
////////                    Date time = sdf.parse(sdf.format(new Date().getTime() + Constants.VALIDATECODE_EXPIRED));
//////////                    JobDetail validateCodeJobDetail = JobBuilder.newJob(ValidateCodeJob.class)
////////                            .withIdentity("validateCodeJob" + codeId, jobGroupName)
////////                            .usingJobData("codeId", codeId)
////////                            .build();
////////                    Trigger validateCodeJobTrigger = TriggerBuilder.newTrigger()
////////                            .withIdentity(
////////                                    TriggerKey.triggerKey("validateCodeJobTrigger"
////////                                            + codeId, triggerGroupName))
////////                            .startAt(time)
////////                            .build();
////////                    scheduler.scheduleJob(validateCodeJobDetail, validateCodeJobTrigger);
////////                    scheduler.start();
////////
////////                } catch (Exception e) {
////////                    e.printStackTrace();
//////                }
//////            }
//////        }).start();
////    }
//
//}
