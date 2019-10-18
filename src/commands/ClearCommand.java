package commands;

import server.StorageManager;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute (), чтобы удалить все элементы из коллекции.
 */
public class ClearCommand extends AbstractProtectedCommand {

    public ClearCommand(StorageManager manager) {
        super(manager, 1, 2);
        setDescription("Очистить коллекцию. clear login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        try {
            getManager().clear(user);
            getManager().save();
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
        return "Коллекция очищена.";
    }
}