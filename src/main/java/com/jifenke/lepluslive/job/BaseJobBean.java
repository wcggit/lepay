package com.jifenke.lepluslive.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class BaseJobBean extends QuartzJobBean {
	protected Logger logger = LoggerFactory.getLogger(BaseJobBean.class);
	
	@Override
	protected abstract void executeInternal(JobExecutionContext context)
			throws JobExecutionException;

	protected ApplicationContext getApplicationContext(final JobExecutionContext jobExecutionContext) {
		try {
			return (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContextKey");
		} catch (SchedulerException e) {
			logger.error("jobExecutionContext.getScheduler().getContext() error!", e);
			throw new RuntimeException(e);
		}
	}

}
