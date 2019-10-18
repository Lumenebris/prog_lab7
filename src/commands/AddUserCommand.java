package commands;

import server.StorageManager;
import tale.Room;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute () для добавления пользователя.
 */
public class AddUserCommand extends AbstractCommand {
    public AddUserCommand(StorageManager manager) {
        super(manager);
        setDescription("Добавляет нового пользователя.\nadd_user {String login} {String password}");
    }

    @Override
    public synchronized String execute(String[] args) {
        String login;
        String password;
        if (args.length == 3) {
            login = args[1];
            password = args[2];
        } else {
            return "Не задан логин или пароль.\nИспользование - add_user login password";
        }
        User user = new User(login, password);
        try {
            if (getManager().add(user)) {
                    getManager().save();
            } else {
                return "Логическая ошибка при создании пользователя";
            }
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
        return "Пользователь добавлен";
    }
}
