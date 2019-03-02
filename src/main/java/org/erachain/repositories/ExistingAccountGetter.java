package org.erachain.repositories;

import org.erachain.utils.crypto.Base58;
import org.erachain.utils.crypto.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Repository
public class ExistingAccountGetter {
    private Map<String, Integer> existingAccounts = new ConcurrentHashMap<>();;
    private JdbcTemplate jdbcTemplate;
    private String INSERT_ACCOUNTS;

    public Map<String, Integer> getExistingAccounts() {
        return existingAccounts;
    }

    @Autowired
    public ExistingAccountGetter (JdbcTemplate jdbcTemplate,
                                  @Value("${FETCH_ACCOUNTS}") String fetchAccounts,
                                  @Value("${INSERT_ACCOUNTS}") String insertAccounts) {
        this.jdbcTemplate = jdbcTemplate;
        this.INSERT_ACCOUNTS = insertAccounts;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(fetchAccounts);
        for (Map<String, Object> row: rows){
            existingAccounts.put((String) row.get("Creator"), (int) row.get("Id"));
        }
    }

    public int insertNewAccount(byte[] key) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps =
                        connection.prepareStatement(INSERT_ACCOUNTS, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, Base58.encode(key));
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setString(3, Crypto.getInstance().getAddress(key));
                return ps;
            }}, keyHolder);
        int id = (int) keyHolder.getKey();
        existingAccounts.put(Crypto.getInstance().getAddress(key), id);
        return id;
    }
}
