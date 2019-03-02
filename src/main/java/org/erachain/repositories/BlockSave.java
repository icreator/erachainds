package org.erachain.repositories;

import org.erachain.utils.loggers.LoggableBlock;
import org.erachain.entities.blocks.Block;
import org.erachain.utils.crypto.Crypto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Date;

//@Repository
@PropertySources({@PropertySource("classpath:queries.properties"), @PropertySource("classpath:db.properties")})
public class BlockSave {

    @Value("${INSERT_INTO_BLOCKS}")
    private String INSERT_INTO_BLOCKS;

    private static int lastId = 0;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Logger logger;

    @LoggableBlock
    public void save(Block block) {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement stm = connection.prepareStatement(INSERT_INTO_BLOCKS);
            stm.setTimestamp(1, new Timestamp(block.getStart().getTime()));
            stm.setTimestamp(2, new Timestamp(block.getEnd().getTime()));
            stm.setInt(3, block.getTransactionCount());
            stm.setInt(4, lastId);
            stm.setBytes(5, block.getBlob(Crypto.getInstance()));
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
    public BlockSave(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
