package org.erachain.utils.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class Log {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @Scope("prototype")
    Logger getLogger(InjectionPoint injectionPoint){

        return logger;
    }
}
