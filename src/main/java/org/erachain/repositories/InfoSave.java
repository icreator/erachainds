package org.erachain.repositories;

import org.erachain.entities.datainfo.DataEra;
import org.erachain.entities.datainfo.DataInfo;
import org.erachain.jsons.ListResponseOnRequestJsonOnlyId;
import org.erachain.jsons.ResponseOnRequestJsonOnlyId;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@PropertySource("classpath:queries.properties")
@PropertySource("classpath:db.properties")
public class InfoSave {

    @Value("${INSERT_INTO_DATA}")
    private String INSERT_INTO_DATA;

    @Value("${FETCH_DATA_BY_ACTREQID}")
    private String FETCH_DATA_BY_ACTREQID;

    @Value("${CHECK_DATA_BY_ACTREQID}")
    private String CHECK_DATA_BY_ACTREQID;

    @Value("${UPDATE_DATA_AFTER_SUBMIT}")
    private String UPDATE_DATA_AFTER_SUBMIT;

    @Value("${UPDATE_DATA_AFTER_ACCEPT}")
    private String UPDATE_DATA_AFTER_ACCEPT;

    @Value("${UPDATE_BLOCK_TRANS}")
    private String UPDATE_BLOCK_TRANS;

    @Value("${UPDATE_DATA_AFTER_SEND_TO_CLIENT}")
    private String UPDATE_DATA_AFTER_SEND_TO_CLIENT;

    @Value("${UPDATE_DATA_ACCEPTED_BY_CLIENT}")
    private String UPDATE_DATA_ACCEPTED_BY_CLIENT;

    @Value("${FETCH_DATA_FOR_CLIENT}")
    private String FETCH_DATA_FOR_CLIENT;

    @Value("${ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID}")
    private String ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID;

    @Value("${UPDATE_DATA_AFTER_RUN}")
    private String UPDATE_DATA_AFTER_RUN;

    @Value("${GET_CHAIN_INFO}")
    private String GET_CHAIN_INFO;

    @Value("${GET_RECORD_RAW_DATA}")
    private String GET_RECORD_RAW_DATA;

    @Value("${ORDER_BY_RUNDATE_DESC_LIMIT}")
    private String RUNDATE_DESC_LIMIT;

    @Value("${ADD_USER_ID_RESTRICTION}")
    private String ADD_USER_ID_RESTRICTION;

    private JdbcTemplate jdbcTemplate;

    private final Logger logger;

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    public InfoSave(JdbcTemplate jdbcTemplate, Logger logger) {
        this.jdbcTemplate = jdbcTemplate;
        this.logger = logger;
    }

    private static final Field[] fields = DataInfo.class.getDeclaredFields();

    public List<DataInfo> fetchDataWhere(int actRequestId) {
        return fetchData(FETCH_DATA_BY_ACTREQID.replace("?", Integer.toString(actRequestId)));
    }

    public int checkDataWhere(int actRequestId) {
        try {
            return dbUtils.checkData(CHECK_DATA_BY_ACTREQID.replace("?", Integer.toString(actRequestId)));
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }
        return 0;
    }

    public void afterRun(DataInfo dataInfo) throws SQLException {
        dataInfo.setAccDate(new Timestamp(System.currentTimeMillis()));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_DATA_AFTER_RUN);
            stm.setTimestamp(1, dataInfo.getRunDate());
            stm.setBytes(2, dataInfo.getData());
            stm.setInt(3, dataInfo.getId());
            stm.executeUpdate();
            stm.close();
        }
    }

    public List<DataInfo> fetchData(String sql) {
        logger.debug(" sql " + sql);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        logger.debug("rows " + rows.size());
        List<DataInfo> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            DataInfo dataInfo = new DataInfo();

            dbUtils.setObj(dataInfo, fields, row);
            list.add(dataInfo);
        }
        return list;
    }

    public String fetchDataForClient(String ident, Map<String, String> params) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            StringBuffer sqlbuf = new StringBuffer(FETCH_DATA_FOR_CLIENT);
            params.keySet().forEach(name -> {
                sqlbuf.append(" ");
                sqlbuf.append(ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID);

            });
            PreparedStatement stm = connection.prepareStatement(sqlbuf.toString());
            int i = 0;
            stm.setString(++i, ident);
            for (String name : params.keySet()) {
                logger.debug(name + " " + params.get(name));
                stm.setString(++i, params.get(name));
                stm.setString(++i, name);
            }
            String data = null;
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                logger.debug(" fetch data for client ident " + ident);
                data = new String(rs.getBytes(1));
                logger.debug(" fetched data for ident " + ident);
                stm.close();
            }
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
        }
    }

    public void afterAcceptEra(DataEra dataEra) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(UPDATE_BLOCK_TRANS);
            stm.setString(1, dataEra.getBlockTrId());
            stm.setInt(2, dataEra.getId());
            stm.executeUpdate();
            stm.close();
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
            }
        }
    }

    public Optional<ResponseOnRequestJsonOnlyId> fetchDataByIdLastBlockDataParams(String ident, Map<String, String> params, long time, String userName) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            StringBuilder sqlbuf = new StringBuilder(GET_CHAIN_INFO);
            sqlbuf.append(" ").append(ADD_USER_ID_RESTRICTION);
            params.keySet().forEach(name -> {
                sqlbuf.append(" ");
                sqlbuf.append(ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID);
            });
            sqlbuf.append(" ").append(RUNDATE_DESC_LIMIT);
            ResponseOnRequestJsonOnlyId json = null;
            try (PreparedStatement stm = connection.prepareStatement(sqlbuf.toString())) {
                int i = 0;
                stm.setString(++i, ident);
                stm.setString(++i, time + "");
                stm.setString(++i, userName);
                for (String name : params.keySet()) {
                    logger.debug(name + " " + params.get(name));
                    stm.setString(++i, name);
                    stm.setString(++i, params.get(name));
                }
                stm.setString(++i, "1");
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        json = new ResponseOnRequestJsonOnlyId(new String(rs.getBytes(2)),
                                new Long(new String(rs.getBytes(1))),
                                Integer.parseInt(new String(rs.getBytes(4))),
                                Integer.parseInt(new String(rs.getBytes(5))));
                    }
                }
            }
            return Optional.ofNullable(json);
        }
    }

    public Optional<String> fetchDataByIdDataLastBlockDataParams(String ident, Map<String, String> params, long time, String userName) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            StringBuilder sqlbuf = new StringBuilder(GET_RECORD_RAW_DATA);
            sqlbuf.append(" ").append(ADD_USER_ID_RESTRICTION);
            params.keySet().forEach(name -> {
                sqlbuf.append(" ");
                sqlbuf.append(ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID);
            });
            sqlbuf.append(" ").append(RUNDATE_DESC_LIMIT);
            String result = null;
            try (PreparedStatement stm = connection.prepareStatement(sqlbuf.toString())) {
                int i = 0;
                stm.setString(++i, ident);
                stm.setString(++i, time + "");
                stm.setString(++i, userName);
                for (String name : params.keySet()) {
                    logger.debug(name + " " + params.get(name));
                    stm.setString(++i, name);
                    stm.setString(++i, params.get(name));
                }
                stm.setString(++i, "1");
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getString(1);
                    }
                }
            }
            return Optional.ofNullable(result);
        }
    }

    public Optional<ListResponseOnRequestJsonOnlyId> fetchDataByIdHistoryBlockDataParams(String ident, Map<String, String> params, long time, int limit, String userName) throws SQLException {
        ListResponseOnRequestJsonOnlyId result = new ListResponseOnRequestJsonOnlyId();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            StringBuilder sqlbuf = new StringBuilder(GET_CHAIN_INFO);
            sqlbuf.append(" ").append(ADD_USER_ID_RESTRICTION);
            params.keySet().forEach(name -> {
                sqlbuf.append(" ");
                sqlbuf.append(ADD_PARAM_RESTRICTION_ACTUAL_REQUEST_ID);
            });
            sqlbuf.append(" ").append(RUNDATE_DESC_LIMIT);
            ResponseOnRequestJsonOnlyId json;
            try (PreparedStatement stm = connection.prepareStatement(sqlbuf.toString())) {
                int i = 0;
                stm.setString(++i, ident);
                stm.setString(++i, time + "");
                stm.setString(++i, userName);
                for (String name : params.keySet()) {
                    logger.debug(name + " " + params.get(name));
                    stm.setString(++i, name);
                    stm.setString(++i, params.get(name));
                }
                stm.setString(++i, limit + "");
                List<ResponseOnRequestJsonOnlyId> responseOnRequestJsonOnlyIds = result.getResponseOnRequestJsonOnlyIds();
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        json = new ResponseOnRequestJsonOnlyId(new String(rs.getBytes(2)),
                                new Long(new String(rs.getBytes(1))),
                                Integer.parseInt(new String(rs.getBytes(3))),
                                Integer.parseInt(new String(rs.getBytes(4))),
                                Integer.parseInt(new String(rs.getBytes(5))));
                        responseOnRequestJsonOnlyIds.add(json);
                    }
                }
            }
            return Optional.of(result);
        }
    }

}
