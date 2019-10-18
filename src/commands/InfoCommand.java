package commands;

import server.StorageManager;

/**
 * Класс переопределяет метод execute () для отображения информации о коллекции.
 */
public class InfoCommand extends AbstractCommand {

    public InfoCommand(StorageManager manager) {
        super(manager);
        setDescription("Выводит информацию о коллекции.");
    }

    @Override
    public String execute(String[] args) {
        return execute();
    }

    @Override
    public synchronized String execute() {
        return getManager().toString();
    }
}
