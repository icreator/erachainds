package org.erachain.service.mydom;

import org.erachain.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MyDomService implements ServiceInterface {

    @Autowired
    ClientMyDom clientMyDom;

    @Autowired
    private JsonServiceMyDom jsonServiceMyDom;

    @Override
    public void login(Map<String, String> params) throws Exception {
        clientMyDom.clientLogin(params);
    }

    @Override
    public List<String> getIdentityList(Map<String, String> params) throws Exception {
        List<String> list = new ArrayList<>();
        //for (String o : clientMyDom.getNetWorkList(params)) {
            list.addAll(clientMyDom.getProblemList(params));
        //}
        return list;
    }

    @Override
    public String getIdentityValues(Map<String, String> params) throws Exception {
        return clientMyDom.getProblemResult(params);
    }

    @Override
    public String setIdentityValues(Map<String, String> params) throws Exception {
//        return clientMyDom.setMeterResult(params);
        return "";
    }

    @Override
    public Boolean checkIdentityValues(Map<String, String> params) throws Exception {
//        return jsonServiceMyDom.checkMeterResult(clientMyDom.checkMeterResult(params));
        return true;
    }
}
