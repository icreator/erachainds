package org.erachain.service.energetik;

import org.erachain.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EnergyService implements ServiceInterface {
    @Autowired
    ClientEnergy clientEnergy;

    @Override
    public void login(Map<String, String> params) {
        clientEnergy.ClientLogin(params);
    }

    @Override
    public List<String> getIdentityList(Map<String, String> params) {
        List<String> list = new ArrayList<>();
        clientEnergy.getNetWorkList().forEach(o -> {
            list.addAll(clientEnergy.getMeterList(o));
        });
        return list;
    }

    @Override
    public String getIdentityValues(Map<String, String> params) {
        return clientEnergy.getMeterResult(params);
    }

}
