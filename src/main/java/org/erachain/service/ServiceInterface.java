package org.erachain.service;

import java.util.List;
import java.util.Map;

public interface ServiceInterface {
    public void login(Map<String, String> params) throws Exception;

    public List<String> getIdentityList(Map<String, String> params) throws Exception;

    public String getIdentityValues(Map<String, String> params) throws Exception;

    public String setIdentityValues(Map<String, String> params) throws Exception;

    public Boolean checkIdentityValues(Map<String, String> params) throws Exception;
}
