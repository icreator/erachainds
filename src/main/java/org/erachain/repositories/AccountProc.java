package org.erachain.repositories;

import org.erachain.entities.account.Account;
import org.erachain.entities.datainfo.DataInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class AccountProc {

//    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${FETCH_ACCOUNTS}")
    private String FETCH_ACCOUNTS;

    @Value("${UPDATE_ACCOUNT_AFTER_RUN}")
    private String UPDATE_ACCOUNT_AFTER_RUN;

    @Autowired
    private Logger logger;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    public  AccountProc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Field[] fields = Account.class.getDeclaredFields();

    private static ConcurrentMap<Integer, Account> cache = new ConcurrentHashMap<>();

    public   List<Account>  getAccounts() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_ACCOUNTS);
        List<Account> list = new ArrayList<>();
        for (Map<String, Object> row: rows){
            Account  account = new Account();
            dbUtils.setObj(account, fields, row);
            list.add(account);
            cache.put(account.getId(), account);
        }
        return list;
    }
    public Account getAccountById(int id)  {
        if (cache.get(id) == null) {
            getAccounts();
        }
        return cache.get(id);
    }
    public void afterRun(Account account) throws SQLException {
        account.setRunDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_ACCOUNT_AFTER_RUN);
            stm.setTimestamp(1, account.getRunDate());
            stm.setInt(2, account.getId());
            stm.executeUpdate();
            cache.put(account.getId(), account);
        }
    }
}
