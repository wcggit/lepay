package com.jifenke.lepluslive.job;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;


/**
 * Created by wcg on 16/3/27.
 */
@Configuration
public class SchedulerConfigration {

  @Inject
  private DataSource dataSource;

  @Autowired
  private ResourceLoader resourceLoader;

//  @Bean(name = "orderDetail")
//  public JobDetailFactoryBean orderDetail(OrderJob scheduledTasks){
//    JobDetailFactoryBean bean = new JobDetailFactoryBean ();
//    bean.setJobClass(OrderJob.class);
//    bean.setDurability(false);
//    return bean;
//  }
//
//  @Bean(name = "orderTrigger")
//  public CronTriggerFactoryBean cronTriggerBean(JobDetailFactoryBean orderDetail){
//    CronTriggerFactoryBean tigger = new CronTriggerFactoryBean ();
//    tigger.setJobDetail(orderDetail.getObject());
//    try {
//      tigger.setCronExpression ("0 0 0 * * ? ");//每5秒执行一次
//    } catch (Exception e) {
//      e.printStackTrace ();
//    }
//    return tigger;
//
//  }

  @Bean
  public SchedulerFactoryBean schedulerFactory(){
    SchedulerFactoryBean bean = new SchedulerFactoryBean ();
    bean.setConfigLocation(resourceLoader.getResource("classpath:quartz.properties"));
    bean.setApplicationContextSchedulerContextKey("applicationContextKey");
    bean.setSchedulerName("lepay");
    bean.setDataSource(dataSource);
   // bean.setTriggers (orderTrigger);
    return bean;
  }
}
