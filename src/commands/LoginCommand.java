package commands;

import server.StorageManager;
import server.UserSessionManager;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute () для аутентификации на сервере.
 */
public class LoginCommand extends AbstractCommand {
    public static final String NAME = "login";

    public LoginCommand(StorageManager manager) {
        super(manager);
        setDescription("Логинится на сервер.\n" + NAME + " {String login} {String password}");
    }

    @Override
    public synchronized String execute(String[] args) {
        String login;
        String password;
        if (args.length == 3) {
            login = args[1];
            password = args[2];
        } else {
            return "Не задан логин или пароль.\nИспользование - login login password";
        }
        try {
            User user = getManager().getUser(login);
            if (user == null) {
                user = new User(login, password);
                getManager().add(user);
                getManager().save();
            }
            user = getManager().authenticate(login, password);
            if (user == null) {
                return "Неверный пароль или имя пользователя.";
            }
            String sessionId = UserSessionManager.getInstance().createSession(user);
            System.out.println("Зарегистрирована новая сессия " + sessionId + " для пользователя " + user.getLogin());
            return sessionId;
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            try {
                getManager().rollback();
            } catch (SQLException ignored) {
            }
            return "Ошибка SQL: " + e.getCause();
        }
    }
}
