package org.erachain.service;

import org.erachain.service.energetik.EnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceFactory {

    @Autowired
    private EnergyService energyService;

    public ServiceInterface getService(String serviceName) {
        if ("Energy".equalsIgnoreCase(serviceName))
            return energyService;
        else
            return null;
    }
}
