package org.erachain.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public  Date getFirst(Date date, String unit){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (unit) {
            case ("month") :
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                break;
            case ("week") :
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                break;
            case ("day") :
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
                break;
            case ("hour") :
                calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
                break;
            case ("minute") :
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
            case ("month") :
                result = DateUtils.addMonths(date, value);
                break;
            case ("week") :
                result = DateUtils.addWeeks(date, value);
                break;
            case ("day") :
                result = DateUtils.addDays(date, value);
                break;
            case ("hour") :
                result = DateUtils.addHours(date, value);
                break;
            case ("minute") :
                result = DateUtils.addMinutes(date, value);
                break;
        }
        return result;
    }

    public Date stringToDate(String strDate) throws Exception {
        Date date;
        try {
            if (strDate.length() == 10) {
                if(strDate.contains("-"))
                    date = simpleDateFormat.parse(strDate);
                else {
                    long time = Long.parseLong(strDate);
                    date = new Date(time + 1000);
                }
            } else {
                if(strDate.contains("-"))
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
}
