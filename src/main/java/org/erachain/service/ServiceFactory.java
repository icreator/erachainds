package org.erachain.service;

import org.erachain.service.energetik.EnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceFactory {

    private  Map<String, ServiceInterface> services = new HashMap<>();

    @Autowired
    private EnergyService energyService;

    public ServiceFactory() {
        services.put("https://app.yaenergetik.ru/api?v2", energyService);
    }

    public ServiceInterface getService(String serviceUrl) {
        return services.get(serviceUrl);
    }
}
