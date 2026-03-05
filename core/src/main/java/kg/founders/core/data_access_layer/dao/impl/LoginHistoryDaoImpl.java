package kg.founders.core.data_access_layer.dao.impl;

import kg.founders.core.data_access_layer.dao.LoginHistoryDao;
import kg.founders.core.entity.LoginHistory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginHistoryDaoImpl implements LoginHistoryDao {
    JdbcTemplate jdbc;

    @Override
    public void save(LoginHistory loginHistory) {
        jdbc.update("insert into " + LoginHistory.TABLE_NAME + " (id, cdt, ip, login)" +
                        " values (NEXTVAL('" + LoginHistory.SEQ_NAME + "'), ?, ?, ?);"
                , loginHistory.getCdt(), loginHistory.getIp(), loginHistory.getLogin());
    }

    @Override
    public boolean isLoginAttemptsExceeded(String login, Integer maxLoginAttempts) {
        var result = jdbc.queryForObject("SELECT" +
                "    CASE  " +
                "        WHEN COUNT(*) > ? THEN 1 " +
                "        ELSE 0 " +
                "    END AS exceeded " +
                " FROM " + LoginHistory.TABLE_NAME +
                " WHERE login = ? ", Boolean.class, maxLoginAttempts, login);
        return result != null && result;
    }

    @Override
    public void deleteAllByLogin(String login) {
        jdbc.update("delete from " + LoginHistory.TABLE_NAME + " where login = ?", login);
    }
}
