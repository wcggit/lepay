package com.jifenke.lepluslive.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wcg on 16/5/13.
 */
@Configuration
public class BaseConfig {


  @Bean
  CurrentTimeDateTimeService currentTimeDateTimeService() {
    return new CurrentTimeDateTimeService();
  }

}
