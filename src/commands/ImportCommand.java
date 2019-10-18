package commands;

import com.google.gson.JsonSyntaxException;
import server.StorageManager;
import tale.JsonSyntaxMistakeException;
import tale.Room;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute () для добавления коллекции из файла, полученного от клиента.
 */
public class ImportCommand extends AbstractProtectedCommand {

    public ImportCommand(StorageManager manager) {
        super(manager, 2, 3);
        setDescription("Добавить объекты из файла на вашем компьютере в коллекцию и сохранить их на сервере.\nimport filename login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        String filename = null;
        if (args.length > 1) {
            filename = args[1];
        } else {
            return "Не задан путь к файлу.\n Использование - import путь_к_JSON-файлу login password.";
        }
        try {
            int added = getManager().add(Room.jsonToRoomHashtable(getManager().getGson(), filename), user);
            getManager().save();
            return "Из вашего файла было добавлено " + added + " элементов.";
        } catch (JsonSyntaxException ex) {
            return "Синтаксическая ошибка JSON в импортированных данных.";
        } catch (JsonSyntaxMistakeException ex) {
            return "Синтаксическая ошибка JSON в импортированных данных.";
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
    }
}