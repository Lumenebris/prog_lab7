package commands;

import server.StorageManager;
import tale.User;

import java.sql.SQLException;

public abstract class AbstractProtectedCommand extends AbstractCommand {
    private final int login_index;
    private final int password_index;

    public AbstractProtectedCommand(StorageManager manager, int login_index, int password_index) {
        super(manager);
        this.login_index = login_index;
        this.password_index = password_index;
    }

    @Override
    public synchronized String execute(String[] args) {
        User user = null;
        try {
            if (args.length > Math.max(login_index, password_index)) {
                user = getManager().authenticate(args[login_index], args[password_index]);
                if (user == null) {
                    return "Неверный пароль или имя пользователя.";
                }
            } else {
                return "Необходимо задать логин и пароль";
            }
            return execute(args, user);
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e;
        }
    }

    public abstract String execute(String[] args, User user);
}
