package commands;

import server.StorageManager;
import tale.User;

import java.sql.SQLException;

/**
 * Класс @code RemoveLowerCommand переопределяет метод @code execute (), чтобы удалить все элементы из коллекции, ключ которых меньше заданного.
 */
public class RemoveLowerCommand extends AbstractProtectedCommand {
    public RemoveLowerCommand(StorageManager manager) {
        super(manager, 2, 3);
        setDescription("Удаляет все элементы из коллекции, ключ которых меньше заданного.\nremove_lower {String key} login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        String skey = null;
        if (args.length > 1) {
            skey = args[1];
        } else {
            return "Не задан ключ key.\n Использование - remove_lower key login password";
        }
        String key = skey.replaceAll("[{}\"]","").trim();
        try {
            if (getManager().size() != 0) {
                int removed = getManager().removeAllWithLowerKey(key, user);
                if (removed == 0) {
                    return ("Совпадений, удовлетворяющих условию, не найдено");
                }
                    getManager().save();
                return "Из коллекции удалено " + removed + " элементов.";
            } else {
                return "Ключ не с чем сравнивать. Коллекция пуста.";
            }
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
    }
}
