package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.request.Request;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
//@PropertySource("classpath:queries.properties")
public class AccountProc {

    //    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${FETCH_ACCOUNTS}")
    private String FETCH_ACCOUNTS;

    @Value("${FETCH_REQUESTS}")
    private String FETCH_REQUESTS;

    @Value("${UPDATE_REQUEST_AFTER_RUN}")
    private String UPDATE_REQUEST_AFTER_RUN;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    public AccountProc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Field[] fields = Account.class.getDeclaredFields();
    private static boolean inited;
    private static ConcurrentMap<Integer, Account> cache = new ConcurrentHashMap<>();
    private static ConcurrentMap<Integer, Request> cacheReq = new ConcurrentHashMap<>();
    private static ConcurrentMap<Integer, List<Request>> cacheListReq = new ConcurrentHashMap<>();
    private static List<Account> listAct;

    public List<Account> getAccounts() {
        if (listAct != null)
            return listAct;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_ACCOUNTS);
        listAct = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Account account = new Account();
            dbUtils.setObj(account, fields, row);
            listAct.add(account);
            cache.put(account.getId(), account);
        }
        return listAct;
    }

    public void setAccounts() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_ACCOUNTS);
        //    List<Account> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Account account = new Account();
            dbUtils.setObj(account, fields, row);
//            list.add(account);
            cache.put(account.getId(), account);
        }
        return;
    }

    public Request getRequestById(int requestId) {
        //       if (!inited)
        //           setRequests();
        return dbUtils.fetchData(Request.class, "Request", requestId);
    }

    //    public   void  setRequests() {
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_REQUESTS);
//        for (Map<String, Object> row: rows){
//            Request  request = new Request();
//            dbUtils.setObj(request, row);
//            List<Request> list = cacheListReq.get(request.getAccountId());
//            if (list == null) {
//                list = new ArrayList<>();
//                cacheListReq.put(request.getAccountId(), list);
//            }
//            list.add(request);
//            cacheReq.put(request.getId(), request);
//        }
//        inited = true;
//        return;
//    }
    public List<Request> getRequests(int accountId) {
        //if (!inited)
        //           setRequests();
        return dbUtils.fetchData(Request.class, "Request", " accountId = " + accountId);
    }

    public Account getAccountById(int id) {
        if (cache.get(id) == null) {
            setAccounts();
        }
        return cache.get(id);
    }

    public void afterRun(Request request) throws SQLException {
        request.setLastRun(new Timestamp(System.currentTimeMillis()));
//        dbUtils.getDataId(UPDATE_REQUEST_AFTER_RUN, request.getLastRun(), request.getId());
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_REQUEST_AFTER_RUN);
            stm.setTimestamp(1, request.getLastRun());
            stm.setInt(2, request.getId());
            stm.executeUpdate();
            stm.close();
            connection.close();
        }
    }
}
