package org.erachain.service;

import java.util.List;
import java.util.Map;

public interface ServiceInterface {
    public void login(Map<String, String> params);

    public List<String> getIdentityList(Map<String, String> params);

    public String getIdentityValues(Map<String, String> params);
}
