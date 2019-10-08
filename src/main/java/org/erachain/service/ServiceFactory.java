package org.erachain.service;

import org.erachain.repositories.AccountProc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceFactory {

    private Map<String, ServiceInterface> services = new HashMap<>();

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        accountProc.getAccounts().forEach((account) -> {
            ServiceInterface serviceInterface = (ServiceInterface) context.getBean(account.getObjectName());
            services.put(account.getAccountUrl(), serviceInterface);
        });
    }

    public ServiceInterface getService(String serviceUrl) {
        return services.get(serviceUrl);
    }
}
