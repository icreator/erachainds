package org.erachain.repositories;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

@Service
public class DbUtils {

    static String fetch = "SELECT * FROM ";

    private final Logger logger;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUtils(JdbcTemplate jdbcTemplate, Logger logger) {
        this.jdbcTemplate = jdbcTemplate;
        this.logger = logger;
    }

    private String getSql(String sql, Object... values) {
        for (Object value : values) {
            String strValue = value.getClass().getSimpleName().endsWith("String") ? "'" + value.toString() + "'" : value.toString();
            sql = StringUtils.replaceOnce(sql, "?", strValue);
        }
        logger.debug(" sql " + sql);
        return sql;
    }

    public List<Map<String, Object>> getDataMapList(String sql, Object... values) throws Exception {
        sql = getSql(sql, values);
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultset = statement.executeQuery();

            if (resultset != null && !resultset.isClosed()) {
                ResultSetMetaData rm = resultset.getMetaData();
                while (resultset.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= rm.getColumnCount(); i++) {
                        String name = rm.getColumnName(i);
                        map.put(name, resultset.getObject(name));
                    }
                    list.add(map);
                }
            }
            statement.close();
        }
        return list;
    }

    public Map<String, Object> getDataMap(String sql, Object... values) throws Exception {
        sql = getSql(sql, values);
        Map<String, Object> map = new HashMap<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultset = statement.executeQuery();

            if (resultset != null && !resultset.isClosed() && resultset.next()) {
                ResultSetMetaData rm = resultset.getMetaData();
                for (int i = 1; i <= rm.getColumnCount(); i++) {
                    String name = rm.getColumnName(i);
                    map.put(name, resultset.getObject(name));
                }
                statement.close();
            }
        }
        return map;
    }

    public <T> T checkData(String sql) throws SQLException {
        logger.debug("Sql " + sql);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultset = statement.executeQuery();

            if (resultset != null && !resultset.isClosed() && resultset.next()) {
//                 // exactly one result so allowed
                return (T) resultset.getObject(1);
            }
            statement.close();
        }
        return null;
    }

    public <T> List<T> getDataList(String sql, Object... values) throws SQLException {
        sql = getSql(sql, values);
        logger.debug("Sql " + sql);
        List<T> list = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultset = statement.executeQuery();

            if (resultset != null && !resultset.isClosed()) {
                while (resultset.next())
                    list.add((T) resultset.getObject(1));
            }
        }
        return list;
    }

    public <T> T getData(String sql, Object... values) throws SQLException {
        sql = getSql(sql, values);
//        logger.debug(" sql " + sql);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultset = statement.executeQuery();

            if (resultset != null && !resultset.isClosed() && resultset.next()) {
                //                // exactly one result so allowed
                return (T) resultset.getObject(1);
            }
        }
        return (T) null;
    }

    public <T> List<T> fetchDataValues(Class<T> clazz, String sql, Object... values) {
        sql = getSql(sql, values);
        return fetchData(clazz, sql);
    }

    public <T> T fetchData(Class<T> clazz, int id) {
        return fetchData(clazz, clazz.getSimpleName(), id);
    }

    public <T> T fetchData(Class<T> clazz, String table, int id) {
        return fetchData(clazz, table, " id = " + id).get(0);
    }

    public <T> List<T> fetchData(Class<T> clazz, String table, String where) {
        return (List<T>) fetchData(clazz, fetch + table + (where = where == null ? "" : " where " + where));
    }

    public <T> List<T> fetchData(Class<T> clazz, String sql) {
//        logger.debug(" sql " + sql);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        //       logger.debug("rows " + rows.size());
        List<T> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Object dataInfo = null;
            try {
                dataInfo = clazz.getConstructor().newInstance();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
            if (dataInfo != null) {
                setObj(dataInfo, row);
                list.add((T) dataInfo);
            }
        }
        return list;
    }

    public void setObj(Object data, Map<String, Object> row) {
        setObj(data, data.getClass().getDeclaredFields(), row);
    }

    public void setObj(Object data, Field[] fields, Map<String, Object> row) {
        Arrays.stream(fields).forEach(f -> {
            if (row.get(f.getName()) != null) {
                try {
                    //                   logger.debug("set field " + f.getName() + " " + row.get(f.getName()));
                    f.setAccessible(true);
                    if (f.getType().getCanonicalName().equals("java.sql.Timestamp")) {
                        f.set(data, new Timestamp((long) row.get(f.getName())));
                    } else
                        f.set(data, row.get(f.getName()));
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        });
        return;
    }

    public String setObjToDb(Object data) {
        return setObjToDb(data, null, true);
    }

    public String setObjToDb(Object data, String table, boolean noId) {
        final StringBuffer campos = new StringBuffer("");
        final StringBuffer valores = new StringBuffer("");
        Arrays.stream(data.getClass().getDeclaredFields()).forEach(f -> {

            try {
                f.setAccessible(true);
                String name = f.getName();
                if (f.get(data) == null)
                    return;
                String value = f.get(data).toString();
//                logger.debug(" name " + name + " value " + value);
                if ("id".equalsIgnoreCase(name))
                    return;
                if (value == null)
                    return;
                if (campos.length() != 0) {
                    campos.append(",");
                    valores.append(",");
                }
                if (f.get(data) instanceof String) {
                    valores.append("'" + value + "'");
                } else if (f.get(data) instanceof Timestamp) {
                    Long val = ((Timestamp) f.get(data)).getTime();
                    valores.append(val.toString());
                } else {
                    valores.append(value);
                }
                campos.append(name);
            } catch (IllegalAccessException ex) {
                logger.error(ex.getMessage());
            }
        });
        table = table == null ? data.getClass().getSimpleName().toLowerCase() : table;
        String sql = "insert into " + table + " (" + campos.toString() + ") values(" + valores.toString() + ");";
        //   logger.debug("sql = " + sql);
        return sql;
    }

    public int exSqlStatement(String sql) {
        return exSqlStatement(sql, false);
    }

    public int exSqlStatement(String sql, boolean upd) {

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(sql);
            int rc = stm.executeUpdate();
            if (upd) {
                logger.debug(" updated " + rc);
                connection.close();
                return rc;
            }
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }

        return 0;
    }

    public int setDbObj(Object data, String table) throws SQLException {
        return setDbObj(data, table, true);
    }

    public int setDbObj(Object data, String table, boolean noId) throws SQLException {
        String sql = setObjToDb(data, table, noId);
        logger.debug(" sql " + sql);
        return exSqlStatement(sql);
    }

    public Set<String> getColumnNames(String table) {
        Set<String> names = new HashSet<>();
        Arrays.stream(getColumnNameArray(table)).forEach(name -> {
            logger.debug(" col " + name);
            names.add(name);
        });
        return names;
    }

    public String[] getColumnNameArray(String table) {
        DatabaseMetaData databaseMetaData = null;
        ResultSet rs = null;
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("Select * from " + table);

            ResultSetMetaData rm = rs.getMetaData();
            String sArray[] = new String[rm.getColumnCount()];
            for (int ctr = 1; ctr <= sArray.length; ctr++) {
                String s = rm.getColumnName(ctr);
                sArray[ctr - 1] = s;
            }
            stmt.close();
            connection.close();
            return sArray;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);

        }
        return null;
    }
}