package commands;

import server.StorageManager;
import tale.Room;
import tale.User;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс переопределяет метод @code execute() для отображения всех элементов из коллекции.
 */
public class ShowCommand extends AbstractProtectedCommand {

    public ShowCommand(StorageManager manager) {
        super(manager, 2, 3);
        setDescription("Выводит все элементы коллекции. show {my - только свои объекты | all - все объекты} login password");
    }

    @Override
    public synchronized String execute(String[] args, User user) {
        boolean showMyObjectsOnly = false;
        if (args.length > 1) {
            switch (args[1]) {
                case "my":
                    showMyObjectsOnly = true;
                    break;
                case "all":
                    showMyObjectsOnly = false;
                    break;
                default:
                    return "Необходимо ввести my или all\n Использование:\n" + getDescription();
            }
        }
        try {
            if (getManager().size() > 0) {
                Set<Map.Entry<String, Room>> rooms =
                        showMyObjectsOnly ?
                            getManager().getRooms(user).entrySet() :
                            getManager().getRooms().entrySet();
                return  rooms.stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .map(Map.Entry::getValue)
                        .map(Room::toString)
                        .collect(Collectors.joining("\n"));
            } else return "Коллекция пуста.";
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
    }
}
