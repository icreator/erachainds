package org.erachain.repositories;

import org.erachain.entities.account.AccountSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class AccountSendersDAO {

    private JdbcTemplate jdbcTemplate;

    @Value("${FETCH_ACCOUNTS_SENDERS}")
    private String FETCH_ACCOUNTS_SENDERS;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    public AccountSendersDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Field[] fields = AccountSender.class.getDeclaredFields();
    private static ConcurrentMap<String, AccountSender> cache = new ConcurrentHashMap<>();
    private static List<AccountSender> listAct;

    public List<AccountSender> getAccountsSenders() {
        if (listAct != null)
            return listAct;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_ACCOUNTS_SENDERS);
        listAct = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            AccountSender accountSender = new AccountSender();
            dbUtils.setObj(accountSender, fields, row);
            listAct.add(accountSender);
            cache.put(accountSender.getIdSender(), accountSender);
        }
        return listAct;
    }

    private void setAccountsSenders() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_ACCOUNTS_SENDERS);
        for (Map<String, Object> row : rows) {
            AccountSender accountSender = new AccountSender();
            dbUtils.setObj(accountSender, fields, row);
            cache.put(accountSender.getIdSender(), accountSender);
        }
    }

    public AccountSender getAccountSenderById(String idSender) {
        if (cache.get(idSender) == null) {
            setAccountsSenders();
        }
        return cache.get(idSender);
    }
}
