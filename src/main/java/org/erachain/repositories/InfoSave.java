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
@PropertySource("classpath:queries.properties")
@PropertySource("classpath:db.properties")
public class InfoSave {

    @Value("${INSERT_INTO_DATA}")
    private String INSERT_INTO_DATA;

    @Value("${FETCH_DATA}")
    private String FETCH_DATA;

    @Value("${UPDATE_DATA_AFTER_SUBMIT}")
    private  String UPDATE_DATA_AFTER_SUBMIT;

    @Value("${UPDATE_DATA_AFTER_ACCEPT}")
    private String UPDATE_DATA_AFTER_ACCEPT;

    @Value("${UPDATE_DATA_AFTER_SEND_TO_CLIENT}")
    private String UPDATE_DATA_AFTER_SEND_TO_CLIENT;

    @Value("${UPDATE_DATA_ACCEPTED_BY_CLIENT}")
    private String UPDATE_DATA_ACCEPTED_BY_CLIENT;

    @Value("${FETCH_DATA_FOR_CLIENT}")
    private String FETCH_DATA_FOR_CLIENT;

//    private static int lastId = 0;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Logger logger;

    @Autowired
    private DbUtils dbUtils;

    private static final Field[] fields = DataInfo.class.getDeclaredFields();

    public List<DataInfo> fetchData() {
        return fetchData(FETCH_DATA);
    }

    public List<DataInfo> fetchData(String sql) {
        logger.info(" sql " +sql);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        logger.info("rows " + rows.size());
        List<DataInfo> list = new ArrayList<>();
        for (Map<String, Object> row: rows){
            DataInfo  dataInfo = new DataInfo();

            dbUtils.setObj(dataInfo, fields, row);
            list.add(dataInfo);
       }
       return list;
    }
    public String fetchDataForClient(String ident, Map<String, String> params) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(FETCH_DATA_FOR_CLIENT);
            int i = 0;
            stm.setString(++ i, ident);
            for (String name : params.keySet()) {
                stm.setString(++ i, params.get(name));
                stm.setString(++ i, name);
            }
            String data = null;
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                logger.info(" fetch data for client ident " + ident);
                data = new String(rs.getBytes(1));
                logger.info(" fetched data for ident " + ident);
                stm.close();
            }
        //    connection.close();
            return data;
        }
    }
    public void afterAccept(DataInfo dataInfo) throws SQLException {
        dataInfo.setAccDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_ACCEPT);
            stm.setTimestamp(1, dataInfo.getAccDate());
            stm.setInt(2, dataInfo.getId());
            stm.executeUpdate();
            stm.close();
        //    connection.close();
        }
    }
    public void afterAcceptedByClient(DataInfo dataInfo) throws SQLException {
        dataInfo.setAcceptClientDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_ACCEPTED_BY_CLIENT);
            stm.setTimestamp(1, dataInfo.getAcceptClientDate());
            stm.setInt(2, dataInfo.getId());
            stm.executeUpdate();
            stm.close();
        //    connection.close();
        }
    }
    public void afterSendToClient(DataInfo dataInfo) throws SQLException {
        dataInfo.setSendToClientDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_SEND_TO_CLIENT);
            stm.setTimestamp(1, dataInfo.getSendToClientDate());
            stm.setInt(2, dataInfo.getId());
            stm.executeUpdate();
            stm.close();
        //    connection.close();
        }
    }
    public void afterSubmit(DataInfo dataInfo) throws SQLException {
        dataInfo.setSubDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_SUBMIT);
            stm.setTimestamp(1, dataInfo.getSubDate());
            stm.setInt(2, dataInfo.getId());
            stm.executeUpdate();
            stm.close();
        //    connection.close();
        }
    }
    public void saveData(DataInfo dataInfo) throws SQLException {
        dataInfo.setRunDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            if (dataInfo.getId() == 0) {
                PreparedStatement stm = connection.prepareStatement(INSERT_INTO_DATA);
                stm.setInt(1, dataInfo.getAccountId());
                stm.setTimestamp(2, dataInfo.getRunDate());
                stm.setString(3, dataInfo.getIdentity());
                stm.setBytes(4, dataInfo.getData());
                stm.setInt(5, dataInfo.getActRequestId());
                stm.executeUpdate();
                ResultSet rs = stm.getGeneratedKeys();
                rs.next();
                dataInfo.setId(rs.getInt(1));
                stm.close();
            //    connection.close();
                return;
            }
        }
    }

    @Autowired
    public InfoSave(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
