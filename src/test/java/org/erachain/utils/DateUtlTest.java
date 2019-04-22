package org.erachain.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DateUtlTest {

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private Logger logger;

    @Test
    public void testDate() {
        Date date = new Date();
        Date firstDateOfMonth = dateUtl.getFirst(date, "month");
        String submitPeriod = "month";
        Date submitDate = dateUtl.getFirst(date, submitPeriod);

        logger.info(firstDateOfMonth.toString());
        submitDate = dateUtl.addUnit(submitDate, submitPeriod,  1);
        logger.info(" submitDate " + submitDate);
        submitDate = dateUtl.addUnit(submitDate, "day",  7);
        logger.info(" submitDate " + submitDate);

    }
}
