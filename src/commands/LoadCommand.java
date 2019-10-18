package commands;

import com.google.gson.JsonSyntaxException;
import server.StorageManager;
import tale.JsonSyntaxMistakeException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Класс переопределяет метод @code execute () для перечитывания файла сервера.
 */
public class LoadCommand extends AbstractCommand {

    public LoadCommand(StorageManager manager) {
        super(manager);
        setDescription("Перечитывает коллекцию из файла сервера.");
    }

    @Override
    public synchronized String execute(String[] args) {
        //File collectionFile = new File("serverCollection.json");
        String notificationToClient = "Возникли проблемы с файлом на сервере. Попробуйте ещё раз позже.";
        try {
            int beginSize = getManager().size();
            getManager().sync();
            return "Коллекция успешно перечитана. Добавлено " + (getManager().size() - beginSize) + " новых элементов.\n";
        } catch (SecurityException e) {
            System.out.println("Файл защищён от чтения.");
            return notificationToClient;
        } catch (FileNotFoundException e) {
            System.out.println("Файл по указанному пути не найден.");
            return notificationToClient;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return notificationToClient;
        } catch (JsonSyntaxMistakeException | JsonSyntaxException ex) {
            System.out.println("Ошибка в JSON: " + ex.getMessage());
            return "Синтаксическая ошибка JSON. Коллекция не может быть загружена.\n";
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
    }
}
