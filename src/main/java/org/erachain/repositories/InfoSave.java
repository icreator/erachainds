package org.erachain.repositories;

import org.erachain.entities.datainfo.DataInfo;
import org.erachain.utils.crypto.Crypto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.Date;

//@Repository
@PropertySources({@PropertySource("classpath:queries.properties"), @PropertySource("classpath:db.properties")})
public class InfoSave {

    @Value("${INSERT_INTO_DATA}")
    private String INSERT_INTO_DATA;

    private static int lastId = 0;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Logger logger;

    public void save(DataInfo dataInfo) {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(INSERT_INTO_DATA);
//            stm.setTimestamp(1, new Timestamp(DataInfo.getStart().getTime()));
//            stm.setTimestamp(2, new Timestamp(DataInfo.getEnd().getTime()));
//            stm.setInt(3, DataInfo.getTransactionCount());
            stm.setInt(4, lastId);
//            stm.setBytes(5, DataInfo.getBlob(Crypto.getInstance()));
            stm.setTimestamp(6, new Timestamp(new Date().getTime()));
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            rs.next();
            lastId = rs.getInt(1);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Autowired
    public InfoSave(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
