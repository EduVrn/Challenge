package challenge.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DataDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getData(String userId) {
        try {
            return jdbcTemplate.queryForObject("select data from Data where userId = ?",
                    new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String data = rs.getString("data");
                    return data;
                }
            }, userId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void setDate(String userId, String data) {
        if (getData(userId) == null) {
            jdbcTemplate.update("INSERT into data (userId, data) VALUES(?,?)", userId, data);
        } else {
            jdbcTemplate.update("UPDATE data SET data = ? WHERE userId = ?", data, userId);
        }
    }
}
