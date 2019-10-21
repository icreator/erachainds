package org.erachain.process;


import org.erachain.entities.ActiveJob;
import org.erachain.entities.JobState;
import org.erachain.entities.account.Account;

import org.erachain.entities.request.ActRequest;
import org.erachain.entities.request.Request;
import org.erachain.repositories.AccountProc;
import org.erachain.repositories.DataClient;
import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class JobMonitor implements InitializingBean {

    @Value("${CHECK_DATA_AFTER_SUBMIT}")
    private String CHECK_DATA_AFTER_SUBMIT;

    @Value("${CHECK_DATA_FOR_SUBMIT}")
    private String CHECK_DATA_FOR_SUBMIT;

    @Value("${CHECK_DATA_AFTER_ACCEPT}")
    private String CHECK_DATA_AFTER_ACCEPT;

    @Value("${CHECK_DATA_AFTER_SEND_TO_CLIENT}")
    private String CHECK_DATA_AFTER_SEND_TO_CLIENT;

    @Autowired
    private DbUtils dbUtils;

    private final Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private ServiceMonitor serviceMonitor;

    @Autowired
    private DateUtl dateUtl;

    @Autowired
    private DataClient dataClient;

    private static final int QUEUE_SIZE = 100;

    private static BlockingQueue<ActiveJob> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    private static boolean startIt = false;


    private final Lock mutex = new ReentrantLock(true);

    public JobMonitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkAccounts();
//        setData();
    }

    public void checkAccounts() {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep((long) 1);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                while (true) {
//                    mutex.lock();
                    logger.debug(" started Job Monitor " + new Date().toString());

                    try {
                        checkData(queue);
                        checkReadyAccounts(queue);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        //logger.error(" Unhandled end Job Monitor");
                    } finally {
//                        mutex.unlock();
                    }
                    queue.stream().forEach(o -> {
                        logger.debug(" Service " + o.getState().toString());
                        switch (o.getState()) {
                            case READY:
                                try {
                                    logger.info("=================Job 1. Receiving data from client================");
                                    Map<String, byte[]> data = dataClient.getDataFromClient(o.getAccountId(), o.getRequestId());
                                    accountProc.afterRun(accountProc.getRequestById(o.getRequestId()));
                                    if (data != null && !data.isEmpty()) {
                                        //logger.info("=================Job 1.1 Saving data from client================");
                                        dataClient.setClientData(o.getRequestId(), data);
                                    }

                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                                break;
                            case STARTED :
                                try {
                                    logger.info("=================Job 2. Sending Data to blockchain================");
                                    serviceMonitor.checkDataSubmit();
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                                break;
                            case DATA_SUB :
                                try {
                                    logger.info("=================Job 3. Check saved data in blockchain================");
                                    serviceMonitor.checkDataAccept();
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                                break;
                            case DATA_ACC :
                                logger.info("=================Job 4. Send tx link to client ================");
                                serviceMonitor.checkSendToClient();
                                break;
                            case INFO_SEND :
                                logger.info("=================Job 5. Check tx link to client ================");
                                serviceMonitor.checkAcceptedByClient();
                                break;
                        }
                        queue.remove();
                    });
                    try {
                        TimeUnit.SECONDS.sleep((long) 1);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        };

        new Thread(server).start();
    }
    public void checkReadyAccounts() {
        checkReadyAccounts(queue);
    }
    public void checkReadyAccounts(BlockingQueue<ActiveJob> queue) {
        List<Account> accounts = accountProc.getAccounts();
        accounts.forEach(account -> {
            List<ActiveJob> activeJobs = checkReadyAccount(account);
            for (ActiveJob activeJob : activeJobs) {
                activeJob.setState(JobState.READY);
                queue.add(activeJob);
                logger.debug(" added ActiveJob " + activeJob.getState().name() + " requestId " +
                        activeJob.getRequestId() + " accountId " + activeJob.getAccountId());
            }
        });
        return;
    }

    public List<ActiveJob> checkReadyAccount(Account account) {
        List<Request> requests = accountProc.getRequests(account.getId());
        List<ActiveJob> activeJobs = new ArrayList<ActiveJob>();
        for (Request request : requests) {
            if (!request.checkTime(dateUtl))
                continue;
//            ActRequest actRequest = dataClient.getCurrActReq(request);
//            if (actRequest != null) {
//                Date date = new Date(actRequest.getDateSubmit().getTime());
//                request.setSubmitDate(date);
//            }
            request.getParams(dbUtils, dateUtl);

            ActiveJob activeJob = new ActiveJob(sequenceNumber.incrementAndGet(), account.getId());
            activeJob.setRequestId(request.getId());
            activeJobs.add(activeJob);
        }
        return activeJobs;
    }
    public void checkData() {
        checkData(queue);
    }
    public void checkData(BlockingQueue<ActiveJob> queue) {

        String[] sqls = {CHECK_DATA_FOR_SUBMIT.replace("?", Long.toString(new Date().getTime())), CHECK_DATA_AFTER_SUBMIT, CHECK_DATA_AFTER_ACCEPT, CHECK_DATA_AFTER_SEND_TO_CLIENT};
        for (int i = sqls.length; i > 0; i --) {
            String sql = sqls[i - 1];
            int records = 0;
            try {
                records = dbUtils.checkData(sql);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            if (records > 0) {
                ActiveJob activeJob = new ActiveJob(sequenceNumber.incrementAndGet(), records);
                activeJob.setState(i);
                queue.add(activeJob);
                logger.debug(" added ActiveJob " + activeJob.getState().name());
            }
        }
    }
}
