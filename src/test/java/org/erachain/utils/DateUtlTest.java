package org.erachain.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DateUtlTest {

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private Logger logger;

    String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    SimpleDateFormat format = new SimpleDateFormat(pattern);

    @Test
    public void testDate() {
        Date date = new Date();
    //   Date firstDateOfMonth = dateUtl.getFirst(date, "hour");
        String submitPeriod = "hour";
        Date submitDate = dateUtl.getFirst(date, submitPeriod);
        logger.info(" submitDate  " + submitDate);
 //       logger.info(firstDateOfMonth.toString());
        submitDate = dateUtl.addUnit(submitDate, submitPeriod,  1);
        logger.info(" submitDate first " + submitDate);
        submitDate = dateUtl.addUnit(submitDate, "minute",  7);
        logger.info(" submitDate added " + submitDate);

    }
    @Test
    public void testDate2() {
        String submitPeriod = "hour";
        String offUnit = "minute";
        int offValue = 7;
        Date date = new Date();
        logger.info(" date  " + getDate(date));
//       date = dateUtl.addUnit(date, offUnit, - offValue);
//        value = format.format(date);
//        paramValue = value;
        Date submitDate = dateUtl.getFirst(date, submitPeriod);
        logger.info(" dateUtl.getFirst  " + getDate(submitDate));
        submitDate = dateUtl.addUnit(submitDate, submitPeriod,  1);
        logger.info(" dateUtl.addUnit hour " + getDate(submitDate));
        submitDate = dateUtl.addUnit(submitDate, offUnit,  offValue - 1);
        logger.info(" dateUtl.addUnit  minute 7-1 " + getDate(submitDate));
    }
    @Test
    public void testDate1() {
    //    logger.info(DateUtils.addHours(new Date(), -7).toString());
        logger.info("result of shift " + dateUtl.addUnit(new Date(), "hour", 10).toString());
    }
    @Test
    public void testDate11() {
            String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        logger.info(format.format(new Date()));
    }
    private String getDate(Date date) {
        return format.format(date);

    }
    @Test
    public void testDate22() {
//        String s = "2015-12-03T17:00:34";
        String s = "2015-12-03";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            TimeZone localTimezone = TimeZone.getDefault();
            logger.info("5:Diff.Local-GMT(" + localTimezone.getID() + "):" + localTimezone.getRawOffset() );


            Date date = dateUtl.stringToDate(s);

            logger.info("date : " + date);
        } catch (Exception ex) {
            logger.info("Exception " + ex);
        }
    }
}
