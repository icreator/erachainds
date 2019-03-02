package org.erachain.utils.loggers;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component("AOPLogger")
public class Logging {

    @Autowired
    private Logger logger;

    private AtomicLong blockCreationTime = new AtomicLong(0);
    private AtomicLong blockNumber = new AtomicLong(0);
    private AtomicLong overallTime = new AtomicLong(0);

    @AfterReturning(value = "@annotation(org.erachain.utils.loggers.LoggableController)", returning = "result")
    public void afterLogging(ResponseEntity result){
        logger.info("Controller Response: {}", result);
    }

    @Before(value = "@annotation(org.erachain.utils.loggers.LoggableBlock)")
    public void beforeBlock(){
        blockCreationTime.set(System.currentTimeMillis());

    }

    @After(value = "@annotation(org.erachain.utils.loggers.LoggableBlock)")
    public void afterBlock(){

        overallTime.addAndGet(System.currentTimeMillis() - blockCreationTime.get());
        blockNumber.incrementAndGet();
        logger.info("Block Number: {}", blockNumber.intValue());
        logger.info("OverallTime: {}", overallTime.intValue());
        logger.info("Block Creating AVERAGE Time: {}", overallTime.intValue() / blockNumber.intValue());
    }

    @AfterThrowing(pointcut = "execution(* org.erachain.*.*.*(..))", throwing = "ex")
    public void myAfterThrowing(JoinPoint joinPoint, Exception ex) {
        logger.error(ex.getMessage());

    }

}