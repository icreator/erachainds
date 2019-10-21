package org.erachain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DateUtl {

    private final Logger logger;

    public DateUtl(Logger logger) {
        this.logger = logger;
    }

    public  Date getFirst(Date date, String unit){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (unit) {
            case ("month") :
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                break;
            case ("week") :
                cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK));
                break;
            case ("day") :
                cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
                break;
            case ("hour") :
                cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
                break;
            case ("minute") :
                cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
                break;
        }

        return cal.getTime();
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
        Date res = null;
        switch (unit) {
            case ("month") :
                res = DateUtils.addMonths(date, value);
                break;
            case ("week") :
                res = DateUtils.addWeeks(date, value);
                break;
            case ("day") :
                res = DateUtils.addDays(date, value);
                break;
            case ("hour") :
                res = DateUtils.addHours(date, value);
                break;
            case ("minute") :
                res = DateUtils.addMinutes(date, value);
                break;
        }
//        logger.info(" addUnit res " + res);
        return res;
    }
    static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static int offset = TimeZone.getDefault().getRawOffset();
    public Date stringToDate(String strDate) throws Exception {

        Date date = null;
        try {
            if (strDate.length() == 10) {
                if(strDate.contains("-"))
                    date = simpleDateFormat.parse(strDate);
                else {   // Unix epoch time in seconds
                    long time = Long.parseLong(strDate);
                    date = new Date(time + 1000);
                }
            } else {
                if(strDate.contains("-"))
                    date = simpleDateTimeFormat.parse(strDate);
                else {  // // Unix epoch time in milliseconds
                    long time = Long.parseLong(strDate);
                    date = new Date(time);
                }
            }
        } catch (ParseException ex) {
            logger.error("Exception " + ex);
            throw new Exception(ex.getMessage());
        }
        return date;
//        return new Date(date.getTime() + offset);
    }
}
