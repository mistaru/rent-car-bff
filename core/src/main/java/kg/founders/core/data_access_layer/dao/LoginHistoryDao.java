package kg.founders.core.data_access_layer.dao;

import kg.founders.core.entity.LoginHistory;

public interface LoginHistoryDao {
    void save(LoginHistory loginHistory);

    boolean isLoginAttemptsExceeded(String login, Integer maxLoginAttempts);

    void deleteAllByLogin(String login);
}