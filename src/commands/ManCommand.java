package commands;

import server.StorageManager;

import java.util.HashMap;

/**
 * Класс переопределяет метод @code execute (), чтобы показать описание команды.
 */
public class ManCommand extends AbstractCommand {

    private HashMap<String, AbstractCommand> commands;

    public ManCommand(StorageManager manager, HashMap<String, AbstractCommand> commands) {
        super(manager);
        setDescription("Показывает руководство к команде.\nman commandName");
        this.commands = commands;
    }

    @Override
    public synchronized String execute(String[] args) {
        String arg = null;
        if (args.length > 1) {
            arg = args[1];
        } else {
            return "Не задано имя компанды.\n Использование - man commandName";
        }
        if (commands.containsKey(arg)) return arg + " - " + commands.get(arg).getDescription();
        else return "Неправильный аргумент.";
    }
}
