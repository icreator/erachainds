package org.erachain.service;

import org.erachain.service.energetik.EnergyService;
import org.erachain.service.mydom.MyDomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceFactory {

    @Autowired
    private BeanLoader beanLoader;

    private  Map<String, ServiceInterface> services = new HashMap<>();

    @Autowired
    public ServiceFactory(EnergyService energyService, MyDomService myDomService) {
        beanLoader.getNamesObjectsServices().stream().forEach((tuple2)->{
//            services.put(tuple2.f0,tuple2.f1);
        });
//        services.put("https://app.yaenergetik.ru/api?v2", energyService);
//        services.put("http://api-admin.testmoydom.ru", myDomService);
    }

    public ServiceInterface getService(String serviceUrl) {
        return services.get(serviceUrl);
    }
}
