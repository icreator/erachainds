package org.erachain.utils;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DateUtl {

    @Autowired
    private Logger logger;

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
        }

        return cal.getTime();
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
        return res;
    }
}
