package commands;

import server.StorageManager;
import tale.Room;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Класс является суперклассом для всех классов команд.
 */
public abstract class AbstractCommand {

    private StorageManager manager; //Позволяет изменить коллекцию.
    private String description; //Содержит краткое руководство к команде.

    public AbstractCommand(StorageManager manager) {
        this.manager = manager;
    }

    /**
     * Метод служит для выполнения кода команды без агрумента.
     * @return строка, которая содержит результат операции.
     */
    public synchronized String execute() {
        return "Отсутствует аргумент.";
    }

    /**
     * Метод служит для выполнения кода команды remove_all.
     * @param room - заданный элемент.
     * @return строка, которая содержит результат операции.
     */
    public synchronized String execute(Room room)  {
        return execute();
    }

    /**
     * Метод служит для выполнения кода команды insert.
     * @param key - заданный ключ.
     * @param room - заданный элемент.
     * @return строка, которая содержит результат операции.
     */
    public synchronized String execute(String key, Room room)  {
        return execute();
    }

    /**
     * Метод служит для выполнения кода команды с агрументом.
     * @param args аргументы команды.
     * @return строка, которая содержит результат операции.
     */
    public synchronized String execute(String[] args) {
        return execute();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public StorageManager getManager() {
        return manager;
    }

    public void setManager(StorageManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractCommand)) return false;
        AbstractCommand that = (AbstractCommand) o;
        return Objects.equals(manager, that.manager) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, description);
    }

    @Override
    public String toString() {
        return "AbstractCommand{" +
                "manager=" + manager +
                ", description='" + description + '\'' +
                '}';
    }
}
