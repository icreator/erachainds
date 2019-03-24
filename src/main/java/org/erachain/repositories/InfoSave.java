package org.erachain.repositories;

import org.erachain.entities.datainfo.DataInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
@PropertySources({@PropertySource("classpath:queries.properties"), @PropertySource("classpath:db.properties")})
public class InfoSave {

    @Value("${INSERT_INTO_DATA}")
    private String INSERT_INTO_DATA;

    @Value("${FETCH_DATA}")
    private String FETCH_DATA;

    @Value("${UPDATE_DATA_AFTER_SUBMIT}")
    private  String UPDATE_DATA_AFTER_SUBMIT;

    @Value("${UPDATE_DATA_AFTER_ACCEPT}")
    private String UPDATE_DATA_AFTER_ACCEPT;

    private static int lastId = 0;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Logger logger;

    @Autowired
    private DbUtils dbUtils;

    private static final Field[] fields = DataInfo.class.getDeclaredFields();

    public List<DataInfo> fetchData() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_DATA);
        logger.info("rows " + rows.size());
        List<DataInfo> list = new ArrayList<>();
        for (Map<String, Object> row: rows){
            DataInfo  dataInfo = new DataInfo();

            dbUtils.setObj(dataInfo, fields, row);
            list.add(dataInfo);
       }
       return list;
    }

    public void afterAccept(DataInfo dataInfo,
                            int blockId, int transId) throws SQLException {
        dataInfo.setAccDate(new Timestamp(System.currentTimeMillis()));
        dataInfo.setBlockId(blockId);
        dataInfo.setTransId(transId);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_ACCEPT);
            stm.setTimestamp(1, dataInfo.getAccDate());
            stm.setInt(2, dataInfo.getBlockId());
            stm.setInt(3, dataInfo.getTransId());
            stm.setInt(4, dataInfo.getId());
            stm.executeUpdate();
        }
    }
    public void afterSubmit(DataInfo dataInfo) throws SQLException {
        dataInfo.setSubDate(new Timestamp(System.currentTimeMillis()));
//        dataInfo.setSignature(signature);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_SUBMIT);
            stm.setTimestamp(1, dataInfo.getSubDate());
            stm.setString(2, dataInfo.getSignature());
            stm.setInt(3, dataInfo.getId());
            stm.executeUpdate();
        }
    }
    public void saveData(DataInfo dataInfo) throws SQLException {
        dataInfo.setRunDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            if (dataInfo.getId() == 0) {
                PreparedStatement stm = connection.prepareStatement(INSERT_INTO_DATA);
                stm.setInt(1, dataInfo.getAccountId());
                stm.setTimestamp(2, dataInfo.getRunDate());
                stm.setBytes(4, dataInfo.getData());
                stm.setString(3, dataInfo.getIdentity());
                stm.executeUpdate();
                ResultSet rs = stm.getGeneratedKeys();
                rs.next();
                lastId = rs.getInt(1);
                dataInfo.setId(lastId);
//                connection.commit();
                connection.close();
                return;
            }
        }

    }

    @Autowired
    public InfoSave(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
