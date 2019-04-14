package org.erachain.process;

import org.apache.commons.lang3.time.DateUtils;
import org.erachain.entities.ActiveJob;
import org.erachain.entities.JobState;
import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.entities.request.Request;
import org.erachain.repositories.AccountProc;
import org.erachain.repositories.DbUtils;
import org.erachain.repositories.InfoSave;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class JobMonitor implements InitializingBean {

    @Value("${CHECK_DATA_AFTER_RUN}")
    private String CHECK_DATA_AFTER_RUN;

    @Value("${CHECK_DATA_AFTER_SUBMIT}")
    private String CHECK_DATA_AFTER_SUBMIT;

    @Value("${CHECK_DATA_AFTER_ACCEPT}")
    private String CHECK_DATA_AFTER_ACCEPT;

    @Value("${CHECK_DATA_AFTER_SEND_TO_CLIENT}")
    private String CHECK_DATA_AFTER_SEND_TO_CLIENT;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Logger logger;

    @Autowired
    private AccountProc accountProc;

    @Autowired
    private InfoSave infoSave;

    @Autowired
    private ServiceMonitor serviceMonitor;

    private static final int QUEUE_SIZE = 100;

    private static BlockingQueue<ActiveJob> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    private static boolean startIt = false;


    private final Lock mutex = new ReentrantLock(true);

    @Override
    public void afterPropertiesSet() throws Exception {
        checkAccounts();
        setData();
    }

    public void setData() {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (queue.isEmpty()) {
                        try {
                            TimeUnit.MINUTES.sleep((long) 1);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    try {
                        mutex.lock();

                        logger.info(" started Service Monitor " + new Date().toString());
                        queue.stream().forEach(o -> {
                            logger.info(" Service " + o.getState().toString());
                            switch (o.getState()) {
                                case READY:
                                    serviceMonitor.checkAccounts();
                                break;
                                case STARTED :
                                    serviceMonitor.checkDataSubmit();
                                break;
                                case DATA_SUB :
                                    serviceMonitor.checkDataAccept();
                                    break;
                                case DATA_ACC :
                                    serviceMonitor.checkSendToClient();
                                    break;
                                case INFO_SEND :
                                    serviceMonitor.checkAcceptedByClient();
                                    break;
//                                case INFO_ACC:
//                                    break;
                            }
                            queue.remove();
                        });
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    } finally {
                        mutex.unlock();
                    }
                }
            }
        };

        new Thread(server).start();
    }

    public void checkAccounts() {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                while (true) {

                    logger.info(" started Job Monitor " + new Date().toString());

                    try {
                        mutex.lock();
                        checkData(queue);
                        checkReadyAccounts(queue);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    } finally {
                        mutex.unlock();
                    }

                    try {
                        TimeUnit.MINUTES.sleep((long) 1);
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
            ActiveJob activeJob = checkReadyAccount(account);
            if (activeJob != null) {
                activeJob.setState(JobState.READY);
                queue.add(activeJob);
                logger.info(" added ActiveJob " + activeJob.getState().name());
            }
        });
        return;
    }

    public ActiveJob checkReadyAccount(Account account) {
        List<Request> requests = accountProc.getRequests(account.getId());
        for (Request request : requests) {
            if (request.checkTime()) {
                return new ActiveJob(sequenceNumber.incrementAndGet(), account.getId());
            }
        }
        return null;
    }
    public void checkData() {
        checkData(queue);
    }
    public void checkData(BlockingQueue<ActiveJob> queue) {

        String[] sqls = {CHECK_DATA_AFTER_RUN, CHECK_DATA_AFTER_SUBMIT, CHECK_DATA_AFTER_ACCEPT, CHECK_DATA_AFTER_SEND_TO_CLIENT};
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
                logger.info(" added ActiveJob " + activeJob.getState().name());
            }
        }
    }
}
