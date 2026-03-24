package kg.founders.core.services.auth.login;

import kg.founders.core.entity.auth.Auth;
import kg.founders.core.model.login.LoginModel;

public interface LoginService {

    String login(LoginModel model, String ip);

    Auth authFromToken(String token);

    String refreshToken(String token);

}
