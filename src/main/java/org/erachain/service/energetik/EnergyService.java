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

    @Autowired
    private JsonService jsonService;

    @Override
    public void login(Map<String, String> params) throws Exception {
        clientEnergy.clientLogin(params);
    }

    @Override
    public List<String> getIdentityList(Map<String, String> params) throws Exception {
        List<String> list = new ArrayList<>();
        for (String o : clientEnergy.getNetWorkList(params)) {
            list.addAll(clientEnergy.getMeterList(o));
        }
        return list;
    }

    @Override
    public String getIdentityValues(Map<String, String> params) throws Exception {
        return clientEnergy.getMeterResult(params);
    }

    @Override
    public String setIdentityValues(Map<String, String> params) throws Exception {
        return clientEnergy.setMeterResult(params);
    }

    @Override
    public Boolean checkIdentityValues(Map<String, String> params) throws Exception {
        return jsonService.checkMeterResult(clientEnergy.checkMeterResult(params));
    }
}
