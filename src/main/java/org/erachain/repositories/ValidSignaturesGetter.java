package org.erachain.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Repository
public class ValidSignaturesGetter {

    private static List<String> validSignatures = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;

    public static List<String> getValidSignatures() {
        return validSignatures;
    }

    @Autowired
    public ValidSignaturesGetter(JdbcTemplate jdbcTemplate, @Value(("${FETCH_VALID_TRANSACTIONS}")) String validTransactions) {
        this.jdbcTemplate = jdbcTemplate;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(validTransactions);
        for (Map<String, Object> row: rows){
            validSignatures.add((String) row.get("Signature"));
        }
    }
}
