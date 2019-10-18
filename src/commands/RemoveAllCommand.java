package commands;

import com.google.gson.JsonSyntaxException;
import server.StorageManager;
import tale.Room;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод @code execute () для удаления элемента из коллекции.
 */
public class RemoveAllCommand extends AbstractProtectedCommand {

    public RemoveAllCommand(StorageManager manager) {
        super(manager, 2, 3);
        setDescription("Удалить из коллекции все элементы, эквивалентные данному.\nremove_all '{elementInJSON}' login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        String jsonroom = null;
        if (args.length > 1) {
            jsonroom = args[1];
        } else {
            return "Не задан elementInJSON.\n Использование - remove_all {elementInJSON} login password";
        }
        try {
            if (getManager().size() != 0) {
                Room room = getManager().getGson().fromJson(jsonroom, Room.class);
                int removed = getManager().removeAll(room, user);
                if (removed == 0) {
                    return ("Совпадений с заданным обьектом не найдено");
                }
                getManager().save();
                return "Из коллекции удалено " + removed + " элементов.";
            }
            else {
                return "Элемент не с чем сравнивать. Коллекция пуста.";
            }
        } catch (JsonSyntaxException ex) {
            return "Синтаксическая ошибка JSON. Не удалось удалить элемент.";
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
    }
}
