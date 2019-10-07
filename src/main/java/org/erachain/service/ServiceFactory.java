package org.erachain.service;

import org.erachain.Start;
import org.erachain.service.energetik.EnergyService;
import org.erachain.service.mydom.MyDomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceFactory {

    @Autowired
    private BeanLoader beanLoader;

    private Map<String, ServiceInterface> services = new HashMap<>();

    @Autowired
    public ServiceFactory(EnergyService energyService, MyDomService myDomService) {
//        ApplicationContext context = new AnnotationConfigApplicationContext(Start.class);
//        beanLoader.getNamesObjectsServices().forEach((tuple2) -> {
//            ServiceInterface serviceInterface = (ServiceInterface) context.getBean(tuple2.f1);
//            services.put(tuple2.f0, serviceInterface);
//        });
        services.put("https://app.yaenergetik.ru/api?v2", energyService);
        services.put("http://api-admin.testmoydom.ru", myDomService);
    }

    public ServiceInterface getService(String serviceUrl) {
        return services.get(serviceUrl);
    }
}
