package commands;

import server.StorageManager;

import java.util.HashMap;

/**
 * Класс переопределяет метод execute () для отображения всех доступных команд
 */
public class HelpCommand extends AbstractCommand {

    private HashMap<String, AbstractCommand> commands;

    public HelpCommand(StorageManager manager, HashMap<String, AbstractCommand> commands) {
        super(manager);
        setDescription("Показывает список доступных команд.");
        this.commands = commands;
    }

    @Override
    public synchronized String execute(String[] args) {
        return commands.keySet().toString() + "\nВведите 'man {command}' для получения справки по к команде.";
    }
}
