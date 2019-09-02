package org.erachain.service;

import org.erachain.service.energetik.EnergyService;
import org.erachain.service.mydom.MyDomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceFactory {

    private  Map<String, ServiceInterface> services = new HashMap<>();

    @Autowired
    public ServiceFactory(EnergyService energyService, MyDomService myDomService) {
        services.put("https://app.yaenergetik.ru/api?v2", energyService);
        services.put("http://api-admin.testmoydom.ru", myDomService);
    }

//    @Autowired
//    public ServiceFactory(MyDomService myDomService) {
//        services.put("http://api-admin.testmoydom.ru", myDomService);
//    }

    public ServiceInterface getService(String serviceUrl) {
        return services.get(serviceUrl);
    }
}
