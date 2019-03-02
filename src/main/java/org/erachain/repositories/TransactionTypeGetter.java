package org.erachain.repositories;

import org.erachain.entities.BaseEntity;
import org.erachain.entities.TransactionInfo;
import org.erachain.utils.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


//@Transactional
//@Repository
@PropertySources({@PropertySource("classpath:queries.properties"), @PropertySource("classpath:db.properties")})
public class TransactionTypeGetter  {

    String FETCH_TYPES;

    String FETCH_INFO;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    ClassUtil classUtil;

    private static final Object obj = new Object();

    private static ConcurrentMap<String, TransactionInfo> cache = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Class> classes = new ConcurrentHashMap<>();
    private static ConcurrentMap<Integer, List<TransactionInfo>> transactionTypes = new ConcurrentHashMap<>();

    public static void clearCash() {
        for (String key : cache.keySet()) {
            if (cache.get(key) != null) {
                synchronized (obj) {
                    Object currentValue = cache.get(key);
                    if (currentValue != null)
                        cache.remove(key, currentValue);
                }
            }
        }
        transactionTypes.clear();
    }

    public boolean checkTransactionType(int type) {
        return transactionTypes.keySet().contains(type);
    }

    public  TransactionInfo getInstance(String key)  {
        return cache.get(key.toUpperCase());
    }

    @Autowired
    public TransactionTypeGetter(JdbcTemplate jdbcTemplate,
                                 @Value("${FETCH_TRANSACTION_TYPES}") String fetchTypes,
                                 @Value("${FETCH_TRANSACTION_INFO}") String fetchInfo) {
        FETCH_TYPES = fetchTypes;
        FETCH_INFO = fetchInfo;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initCash() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_TYPES);
        for (Map<String, Object> row: rows){
            int type = (int) row.get("TypeId");
            List<TransactionInfo> list = getTransactionInfo(type);

            if (list != null && !list.isEmpty())
                transactionTypes.put(type, list);

        }
    }

    public  TransactionInfo getInstance(int type, String name)  {
        return cache.get(type + "_" + name.toUpperCase());
    }

    public List<TransactionInfo> getTransactionsListInfo(int type) {
        return transactionTypes.get(type);
    }

    private List<TransactionInfo> getTransactionInfo(int type) {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_INFO, new Object[]{type});
        List<TransactionInfo> transactionInfoList = new ArrayList<>();
        for (Map<String, Object> row: rows){
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setFieldLength((int) row.get("FieldLength"));
            transactionInfo.setFieldName(((String) row.get("FieldName")));
            transactionInfo.setFieldType(((String) row.get("FieldType")));
            if ("Class".equalsIgnoreCase(transactionInfo.getFieldType())) {
                classes.put(transactionInfo.getFieldName().toUpperCase(),classUtil.load(transactionInfo.getFieldName()));
            }
            if ("Length".equalsIgnoreCase(transactionInfo.getFieldType())) {
                transactionInfo.setMaxValue((int) row.get("MaxValue"));
            }
            cache.put(row.get("TypeId").toString() + "_" + transactionInfo.getFieldName().toUpperCase(), transactionInfo);
            transactionInfoList.add(transactionInfo);
        }

        return transactionInfoList;
    }

    public BaseEntity getCustomObject(String fieldName) {
        return classUtil.getInstFromClass(classes.get(fieldName.toUpperCase()));
    }
}
