package org.erachain.process;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.repositories.AccountProc;
import org.erachain.repositories.InfoSave;
import org.erachain.service.ServiceFactory;
import org.erachain.service.ServiceInterface;
import org.erachain.service.eraService.EraClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:custom.properties")
public class ServiceMonitor {
    @Autowired
    private Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private ServiceFactory serviceFactory;

    @Autowired
    private EraClient eraClient;

    @Value("${Service_Url}")
    private String Service_Url;

    public void checkAccounts() {
        List<Account> accounts = accountProc.getAccounts();
        accounts.stream().forEach(account -> {
            if(account.checkTime()) {
                account.reset();
                Map<String, String> params = account.getParams();
                logger.info(" params " + params.get(Service_Url));
                ServiceInterface service = serviceFactory.getService(params.get(Service_Url));
                logger.info(" account service " + service);
                List<String> idents = service.getIdentityList(params);
                idents.stream().forEach(ident -> {
                    params.put(account.getIdentityName(), ident);
                    String json = service.getIdentityValues(params);
                    if (json != null && !json.isEmpty()) {
                        DataInfo dataInfo = new DataInfo();
                        dataInfo.setRunDate(new Timestamp(System.currentTimeMillis()));
                        dataInfo.setAccountId(account.getId());
                        dataInfo.setIdentity(ident);
                        dataInfo.setData(json.getBytes());
                        try {
                            infoSave.saveData(dataInfo);
                            account.addReceived();
                            logger.info("data saved  for ident " + ident + " " + dataInfo.getRunDate());
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                });
                if (account.getLastReceived() > 0) {
                    try {
                        accountProc.afterRun(account);
                        logger.info(" account updated after run " + account.getUser());
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }
            }
        });
    }
    public void checkData() {
        List<DataInfo> dataInfos = infoSave.fetchData();

        dataInfos.stream().forEach(dataInfo ->{
            if(dataInfo.getRunDate() != null && dataInfo.getSubDate() == null) {
                Account account = accountProc.getAccountById(dataInfo.getAccountId());
                eraClient.setSignature(dataInfo, account);
                try {
                    infoSave.afterSubmit(dataInfo);
                } catch (SQLException e) {
                    logger.info(e.getMessage());
                }
            }
        });
    }
}
