package server;

import com.google.gson.Gson;
import tale.JsonSyntaxMistakeException;
import tale.Room;
import tale.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public interface StorageManager {
    void save() throws SQLException;

    void rollback() throws SQLException;

    void clear(User user) throws SQLException;

    int size() throws SQLException;

    boolean add(Room room, User user) throws SQLException;

    int add(Map<String, Room> rooms, User user) throws SQLException;

    void sync() throws IOException, JsonSyntaxMistakeException;

    boolean remove(String key, User user) throws SQLException;

    int removeAll(Room room, User user) throws SQLException;

    int removeAllWithLowerKey(String key, User user) throws SQLException;

    Map<String, Room> getRooms() throws SQLException;

    Map<String, Room> getRooms(User user) throws SQLException;

    boolean add(User user) throws SQLException;

    User getUser(String login) throws SQLException;

    User authenticate(String login, String password) throws SQLException;

    Gson getGson();
}
