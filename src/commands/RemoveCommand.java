package commands;

import server.StorageManager;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод @code execute () для удаления элемента из коллекции по заданному ключу.
 */
public class RemoveCommand extends AbstractProtectedCommand {

    public RemoveCommand(StorageManager manager) {
        super(manager, 2, 3);
        setDescription("Удаляет элемент из коллекции по заданному ключу.\nremove {String key} login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        String key = null;
        if (args.length > 1) {
            key = args[1];
        } else {
            return "Не задан ключ key.\n Использование remove {String key} login password";
        }
        String processed_key = key.replaceAll("[{}\"]","").trim();
        try {
            if (getManager().size() > 0) {
                if (!getManager().remove(processed_key, user)) {
                    return "Такого элемента нет в коллекции: " + processed_key + ".";
                }
                getManager().save();
            } else {
                return "Ключ не с чем сравнивать. Коллекция пуста.";
            }
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e;
        }
        return "Элемент успешно удален.";
    }
}