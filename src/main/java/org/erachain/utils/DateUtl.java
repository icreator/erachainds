package org.erachain.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateUtl {

    private final Logger logger;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public DateUtl(Logger logger) {
        this.logger = logger;
    }

    public Date getFirst(Date date, String unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (unit) {
            case ("month"):
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                break;
            case ("week"):
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                break;
            case ("day"):
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
                break;
            case ("hour"):
                calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
                break;
            case ("minute"):
                calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
                break;
        }
        return calendar.getTime();
    }

    public Date getAlign(Date date, String unit) {
        if (unit.toLowerCase().contains("hour")) {
            return getFirst(date, "hour");
        } else if (unit.toLowerCase().contains("minute")) {
            return getFirst(date, "minute");
        }
        return date;
    }

    public Date addUnit(Date date, String unit, int value) {
        Date result = null;
        switch (unit) {
            case ("month"):
                result = DateUtils.addMonths(date, value);
                break;
            case ("week"):
                result = DateUtils.addWeeks(date, value);
                break;
            case ("day"):
                result = DateUtils.addDays(date, value);
                break;
            case ("hour"):
                result = DateUtils.addHours(date, value);
                break;
            case ("minute"):
                result = DateUtils.addMinutes(date, value);
                break;
        }
        return result;
    }

    public Date stringToDate(String strDate) throws Exception {
        Date date;
        try {
            if (strDate.length() == 10) {
                if (strDate.contains("-"))
                    date = simpleDateFormat.parse(strDate);
                else {
                    long time = Long.parseLong(strDate);
                    date = new Date(time + 1000);
                }
            } else {
                if (strDate.contains("-"))
                    date = simpleDateTimeFormat.parse(strDate);
                else {
                    long time = Long.parseLong(strDate);
                    date = new Date(time);
                }
            }
        } catch (ParseException ex) {
            logger.error("Exception parse date", ex);
            throw new Exception(ex.getMessage());
        }
        return date;
    }

    public Date reduceToLowerBound(Date date, String unit, ZoneId zoneId) {
//        LocalDateTime result = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime result = LocalDateTime.ofInstant(date.toInstant(), zoneId);

        switch (unit) {
            case ("month"): {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                return calendar.getTime();
            }
            case ("week"): {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                return calendar.getTime();
            }
            case ("day"):
                result = result.truncatedTo(ChronoUnit.DAYS);
                break;
            case ("hour"):
                result = result.truncatedTo(ChronoUnit.HOURS);
                break;
            case ("minute"):
                result = result.truncatedTo(ChronoUnit.MINUTES);
                break;
        }
        ZonedDateTime zonedDateTime = result.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public long getmilliSecondsRunPeriod(String runPeriod) {
        long result = 0;
        String[] period = runPeriod.split("_");
        int value = 1;
        String periodRun = runPeriod;
        if (period.length > 1) {
            value = Integer.parseInt(period[0]);
            periodRun = period[1];
        }
        switch (periodRun) {
            case ("month"):
                result = 60 * 60 * 24 * 30;
                break;
            case ("week"):
                result = 60 * 60 * 24 * 7;
                break;
            case ("day"):
                result = 60 * 60 * 24;
                break;
            case ("hour"):
                result = 60 * 60;
                break;
            case ("minute"):
                result = 60;
                break;
        }
        return 1000 * result * value;
    }

}
