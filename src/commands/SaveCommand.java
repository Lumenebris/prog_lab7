package commands;

import server.StorageManager;

import java.sql.SQLException;

/**
 * Класс переопределяет метод @code execute () для сохранения коллекции в файл сервера.
 */
public class SaveCommand extends AbstractCommand {

    public SaveCommand(StorageManager manager) {
        super(manager);
        setDescription("Сохраняет коллекцию в файл сервера.");
    }

    @Override
    public synchronized String execute(String[] args) {
        try {
            getManager().save();
            return "Изменения сохранены.";
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e;
        } catch (Exception ex) {
            return "Произошла непредвиденная ошибка на сервере. Коллекция не может быть сохранена. Попробуйте ещё раз позже.";
        }
    }
}
