package com.jifenke.lepluslive.global.config;

/**
 * Created by wcg on 16/5/13.
 */

import org.springframework.data.auditing.DateTimeProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AuditingDateTimeProvider implements DateTimeProvider {

  private final CurrentTimeDateTimeService dateTimeService;

  public AuditingDateTimeProvider(CurrentTimeDateTimeService dateTimeService) {
    this.dateTimeService = dateTimeService;
  }

  @Override
  public Calendar getNow() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateTimeService.getCurrentDateAndTime());
    return calendar;
  }
}
