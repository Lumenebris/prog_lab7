package server;

import com.google.gson.*;
import tale.JsonSyntaxMistakeException;
import tale.Room;
import tale.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class DbStorageManager implements StorageManager {

    private Connection db;
    private Gson gson;

    public DbStorageManager(String conn_param, String login, String password ) throws SQLException {
         this.db = DriverManager.getConnection(conn_param, login, password);
         db.setAutoCommit(false);
         this.gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime .class, new JsonDeserializer<ZonedDateTime>() {
             @Override
             public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext
                     jsonDeserializationContext) throws JsonParseException {
                 return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
             }
         }).create();
    }

    @Override
    public void save() throws SQLException {
        db.commit();
    }

    @Override
    public void clear(User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("DELETE FROM lena_laba7.rooms WHERE user_id = ?");
        st.setInt(1, user.getId());
        st.execute();
    }

    @Override
    public int size() throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT count(1) AS room_count FROM lena_laba7.rooms");
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("room_count");
        }
        rs.close();
        st.close();
        return 0;
    }

    @Override
    public boolean add(Room room, User user) throws SQLException {
        if (getRoom(room.getShape()) != null) {
            return false;
        }
        PreparedStatement st = db.prepareStatement("INSERT INTO lena_laba7.rooms (" +
                "user_id, created, floor, number, shape, wall_material, x, y)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        st.setInt(1, user.getId());
        st.setTimestamp(2, Timestamp.valueOf(room.getCreated().toLocalDateTime()));
        st.setInt(3, room.getFloor());
        st.setInt(4, room.getNumber());
        st.setString(5, room.getShape());
        st.setString(6, room.getWall().getMaterial());
        st.setInt(7, room.getX());
        st.setInt(8, room.getY());

        st.execute();
        int result = st.getUpdateCount();
        st.close();
        return result == 1;
    }

    private Room getRoom(String shape) throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT id, " +
                "user_id, created, floor, number, shape, wall_material, x, y FROM lena_laba7.rooms" +
                " WHERE shape = ?");
        st.setString(1, shape);
        ResultSet rs = st.executeQuery();
        Room room = null;
        if (rs.next()) {
            room = new Room(rs);
        }
        rs.close();
        st.close();
        return room;
    }

    @Override
    public int add(Map<String, Room> rooms, User user) throws SQLException {
        int added = 0;
        for (Room room : rooms.values()) {
            if (add(room, user)) {
                ++added;
            }
        }
        return added;
    }

    @Override
    public void sync() throws IOException, JsonSyntaxMistakeException {
        // Игнорируем
    }

    @Override
    public boolean remove(String key, User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("DELETE FROM lena_laba7.rooms " +
                " WHERE user_id = ? AND shape = ?");
        st.setInt(1, user.getId());
        st.setString(2, key);
        st.execute();
        int deleted = st.getUpdateCount();
        st.close();
        return deleted > 0;
    }

    @Override
    public int removeAll(Room room, User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("DELETE FROM lena_laba7.rooms " +
                " WHERE user_id = ? AND shape = ?");
        st.setInt(1, user.getId());
        st.setString(2, room.getShape());
        st.execute();
        int deleted = st.getUpdateCount();
        st.close();
        return deleted;
    }

    @Override
    public int removeAllWithLowerKey(String key, User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("DELETE FROM lena_laba7.rooms " +
                " WHERE user_id = ? AND shape < ?");
        st.setInt(1, user.getId());
        st.setString(2, key);
        st.execute();
        int deleted = st.getUpdateCount();
        st.close();
        return deleted;
    }

    @Override
    public Map<String, Room> getRooms() throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT id, " +
                "user_id, created, floor, number, shape, wall_material, x, y FROM lena_laba7.rooms");
        ResultSet rs = st.executeQuery();
        Map<String, Room> rooms = new HashMap<>();
        while (rs.next()) {
            Room room = new Room(rs);
            rooms.put(room.getShape(), room);
        }
        rs.close();
        st.close();
        return rooms;
    }

    @Override
    public Map<String, Room> getRooms(User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT id, " +
                "user_id, created, floor, number, shape, wall_material, x, y FROM lena_laba7.rooms" +
                " WHERE user_id = ?");
        st.setInt(1, user.getId());
        ResultSet rs = st.executeQuery();
        Map<String, Room> rooms = new HashMap<>();
        while (rs.next()) {
            Room room = new Room(rs);
            rooms.put(room.getShape(), room);
        }
        rs.close();
        st.close();
        return rooms;
    }

    @Override
    public boolean add(User user) throws SQLException {
        PreparedStatement st = db.prepareStatement("INSERT INTO lena_laba7.users (login, password)" +
                " VALUES (?, ?)");
        st.setString(1, user.getLogin());
        st.setString(2, user.getEncryptedPassword());
        st.execute();
        int result = st.getUpdateCount();
        st.close();
        return result == 1;
    }

    @Override
    public User authenticate(String login, String password) throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT id, login, password FROM lena_laba7.users" +
                " WHERE login = ? AND password = ?");
        User checked_user = new User(login, password);
        st.setString(1, checked_user.getLogin());
        st.setString(2, checked_user.getEncryptedPassword());
        ResultSet rs = st.executeQuery();
        User authenticated_user = null;
        if (rs.next()) {
            authenticated_user = new User(rs);
        }
        rs.close();
        st.close();
        return authenticated_user;
    }

    @Override
    public Gson getGson() {
        return gson;
    }
}
