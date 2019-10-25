package tale;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;

public class Room implements Serializable {
    private int id;
    private int floor;
    private int number;
    private String shape;
    private Wall walls;
    private int x;
    private int y;
    private OffsetDateTime created;

    public Room (int floor, int number, String shape, Wall walls, int x, int y) {
        this.floor = floor;
        this.number = number;
        this.shape = shape;
        this.walls = walls;
        this.x = x;
        this.y = y;
        this.created = OffsetDateTime.now();
    }

    public Room(int floor, int number, String shape, Wall walls, int x, int y, OffsetDateTime created) {
        this.floor = floor;
        this.number = number;
        this.shape = shape;
        this.walls = walls;
        this.x = x;
        this.y = y;
        this.created = created;
    }

    public Room(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.floor = rs.getInt("floor");
        this.number = rs.getInt("number");
        this.shape = rs.getString("shape");
        this.walls = new Wall(rs.getString("wall_material"));
        this.x = rs.getInt("x");
        this.y = rs.getInt("y");
        this.created = OffsetDateTime.ofInstant(
                rs.getTimestamp("created").toInstant(),
                ZoneId.systemDefault());
    }

    public String getParameters() {
        return "этаж: " + floor +
                ",номер: " + number;
    }

    /**
     * Добавляет элементы в коллекцию
     * @param jsonRoomHashtable - коллекция
     */
    public static Hashtable<String,Room> jsonToRoomHashtable(Gson gson, String jsonRoomHashtable) throws tale.JsonSyntaxMistakeException {
        try {
            Hashtable<String,Room> collection = new Hashtable<>();
            int noInitializedCount = 0;
            if (jsonRoomHashtable.length() != 0) {
                Room[] roomArray = gson.fromJson(jsonRoomHashtable, Room[].class);  //Массив объектов tale.Room
                for (Room i : roomArray) {
                    if ((i != null) && (i.getFloor() != 0) && (i.getNumber() != 0) && (i.getWall() != null) && (i.getWall().getMaterial() != null)) {
                        collection.put(i.getShape(), i);
                    }else noInitializedCount++;
                }
            }
            if (noInitializedCount > 0) System.out.println("Найдено " + noInitializedCount + " не полностью инициализированных элементов");
            return collection;
        }catch (JsonSyntaxException ex){
            throw new tale.JsonSyntaxMistakeException();
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"floor\": " + floor +
                ", \"number\": " + number +
                ", \"shape\": \"" + shape + "\"" +
                ", \"walls\": {\"material\": \"" + walls.getMaterial() +  "\"" + "}" +
                ", \"x\": " + x +
                ", \"y\": " + y +
                ", \"created\": " + created +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        if (floor != room.floor) return false;
        if (number != room.number) return false;
        if (walls != room.walls) return false;
        if (shape != room.shape) return false;
        if (created != room.created) return false;
        boolean c = obj.hashCode()== this.hashCode() ? true : false;
        return c;
    }

    @Override
    public int hashCode() {
        int result = floor;
        result = 31 * result + number;
        result = 31 * result + shape.hashCode();
        result = 31 * result + walls.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + created.hashCode();
        return result;
    }

    public boolean isIdentical(Room room) {
        return     floor == room.floor
                && number == room.number
                && shape.equals(room.shape)
                && walls.getMaterial().equals(room.walls.getMaterial())
                && x == room.x
                && y == room.y;
    }

    public boolean isValid() {
        return     floor != 0
                && number != 0
                && walls != null
                && walls.getMaterial() != null
                && created != null
                && x != 0
                && y != 0;
    }

    public int getFloor() {
        return floor;
    }

    public int getNumber() {
        return number;
    }

    public String getShape() {
        return shape;
    }

    public Wall getWall() {
        return walls;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public int getId() {
        return id;
    }
}
