package commands;

import com.google.gson.JsonSyntaxException;
import server.StorageManager;
import tale.Room;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute () для добавления элемента в коллекцию по заданному ключу.
 */
public class InsertCommand extends AbstractProtectedCommand {
    public InsertCommand(StorageManager manager) {
        super(manager, 3, 4);
        setDescription("Добавляет элемент в коллекцию по заданному ключу.\ninsert {String key} '{elementInJSON}' login password\n" +
                "   где key - ключ, соответвующий shape элемента\n" +
                "       elementInJSON - элемент комнаты в формате JSON в одинарных кавычках(')");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        String key = null;
        String element = null;
        if (args.length > 2) {
             key = args[1].replaceAll("[{}\"]","").trim();
             element = args[2];
        } else {
            return "Не задан ключ.\nИспользование:\n" + getDescription();
        }
        if (element.startsWith("'") && element.endsWith("'")) {
            element = element.substring(1, element.length() - 1);
        } else {
            return "Параметр elementInJSON должен быть в одинарных кавычках. Использование:\n" + getDescription();
        }
        try {
            Room room = getManager().getGson().fromJson(element, Room.class);
            if ((room != null) && room.isValid()) {
                if (key.equals(room.getShape())) {
                    try {
                        if (!getManager().add(room, user)) {
                            return "Такой ключ уже существует.";
                        }
                        getManager().save();
                    } catch (SQLException e) {
                        System.out.println("SQL error = " + e);
                        return "Ошибка SQL: " + e;
                    }
                    return "Элемент успешно добавлен";
                } else {
                    return "Ключ должен совпадать со значением shape.";
                }
            } else {
                return "Объект неинициализирован, требуется корректный JSON-объект в одинарных кавычках, например:\n\"" +
                        "'{\"floor\": 13,\"number\": 666, \"shape\": \"star\", \"walls\": {\"material\": \"obsidian\"}, \"x\": 6,\"y\": 6, \"created\": \"2011-10-13T00:00:00+04:00\"}'";
            }
        } catch (JsonSyntaxException ex) {
            return "Не удалось прочитать JSON в elementInJSON, ошибка - " + ex.getMessage();
        }
    }
}
