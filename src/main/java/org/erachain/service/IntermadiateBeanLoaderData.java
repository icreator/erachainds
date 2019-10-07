package org.erachain.service;

import org.apache.flink.api.java.tuple.Tuple2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IntermadiateBeanLoaderData {
    @Bean
    public DataBeanLoader receiveObject() {
        return new DataBeanLoader();
    }
    @Bean
    public List<Tuple2<String, String>> receiveObjectList() {
        return new ArrayList<>();
    }
}
