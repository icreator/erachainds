package org.erachain.repositories;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;

@Service
public class DbUtils {

    @Autowired
    private Logger logger;

    public void setObj(Object dataInfo, Field[] fields, Map<String, Object> row) {
        Arrays.stream(fields).forEach(f -> {
            if (row.get(f.getName()) != null) {
                try {
                    logger.info("set field " + f.getName() + " " + row.get(f.getName()));
                    f.setAccessible(true);
                    if (f.getType().getCanonicalName().equals("java.sql.Timestamp")) {
                        f.set(dataInfo, new Timestamp((long) row.get(f.getName())));
                    } else
                        f.set(dataInfo, row.get(f.getName()));
                } catch (IllegalAccessException e) {
                    logger.info(e.getMessage());
                }
            }
        });
        return ;
    }
}
