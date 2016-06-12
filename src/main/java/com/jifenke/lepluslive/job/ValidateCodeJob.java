package com.jifenke.lepluslive.job;

import com.jifenke.lepluslive.lejiauser.service.ValidateCodeService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * 验证码过期定时任务
 * Created by zhangwen on 16/04/28.
 */
public class ValidateCodeJob extends BaseJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        ApplicationContext applicationContext = super.getApplicationContext(context);
        ValidateCodeService validateCodeService = applicationContext.getBean("validateCodeService", ValidateCodeService.class);
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long codeId = dataMap.getLong("codeId");

        validateCodeService.changeValideteCodeState(codeId);
    }
}
