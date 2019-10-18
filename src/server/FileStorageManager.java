package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;

import tale.JsonSyntaxMistakeException;
import tale.Room;
import tale.User;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Обеспечивает доступ к коллекции.
 */

public class FileStorageManager implements StorageManager {
    private Hashtable<String, Room> collection;
    private Gson gson;
    private Date createdDate;
    private File servCollection;

    {
        collection = new Hashtable<String, Room>();
        gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
            }
        }).create();
        Type collectionType = new TypeToken<Hashtable<String, Room>>() {
        }.getType();
    }

    /**
     * Предоставляет доступ к коллекции и связанному с ней файлу.
     * @param file - заданный файл
     */
    FileStorageManager(File file) {
       servCollection = file;
            try (BufferedReader collectionReader = new BufferedReader(new FileReader(servCollection))) {
                System.out.println("Загрузка коллекции " + servCollection.getAbsolutePath());
                String nextLine;
                StringBuilder result = new StringBuilder();
                while ((nextLine = collectionReader.readLine()) != null) result.append(nextLine);
                String jsonString = new String(result);
                try {
                    //collection = gson.fromJson(result.toString(), collectionType);
                    collection.putAll(Room.jsonToRoomHashtable(gson, jsonString));
                    System.out.println("Коллекция успешно загружена. Добавлено " + collection.size() + " элементов.");
                } catch (JsonSyntaxException | JsonSyntaxMistakeException ex) {
                    System.err.println("Ошибка синтаксиса JSON. " + ex.getMessage());
                    //System.exit(1);
                }
            }catch (IOException e) {
                System.err.println("Что-то не так с файлом.");
                //System.exit(1);
            }
        createdDate = new Date();
    }


    /**
     * Записывает элементы коллекции в файл. Так как необходим нескольким командам, реализован в этом классе.
     */
    @Override
    public void save() {
        ArrayList<String> counter = new ArrayList<>();
        try (BufferedWriter writerToFile = new BufferedWriter(new FileWriter(servCollection))) {
            writerToFile.write("[");
            collection.forEach((k, v) -> {
                try {
                    counter.add(k);
                    if (counter.size() != collection.size()) writerToFile.write(gson.toJson(collection.get(k)) + ",");
                    else writerToFile.write(gson.toJson(collection.get(k)) + "]");
                } catch (IOException ex) {
                    System.out.println("Сохранение коллекции не удалось.");
                }
            });
        } catch (Exception ex) {
            System.out.println("Возникла непредвиденная ошибка. Коллекция не может быть сохранена.");
        }
    }

    @Override
    public void clear(User user) {
        collection.clear();
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean add(Room room, User user) {
        String key = room.getShape();
        if (!collection.containsKey(key)) {
            collection.put(key, room);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public int add(Map<String, Room> rooms, User user) {
        collection.putAll(rooms);
        return rooms.size();
    }

    @Override
    public void sync() throws IOException, JsonSyntaxMistakeException {
        File collectionFile = servCollection;
        String extension = collectionFile.getAbsolutePath().substring(collectionFile.getAbsolutePath().lastIndexOf(".") + 1);
        if (!collectionFile.exists() | collectionFile.length() == 0 | !extension.equals("json")) {
            throw new FileNotFoundException();
        }
        if (!collectionFile.canRead()) throw new SecurityException();
        try (BufferedReader inputStreamReader = new BufferedReader(new FileReader(collectionFile))) {
            String nextLine;
            StringBuilder result = new StringBuilder();
            while ((nextLine = inputStreamReader.readLine()) != null) result.append(nextLine);
            String jsonString = new String(result);
            Hashtable<String, Room> addedRoom = Room.jsonToRoomHashtable(gson, jsonString);
            addedRoom.forEach((k, v) -> {
                if (!collection.containsKey(k)) {
                    collection.put(k, v);
                }
            });
        }
    }

    @Override
    public boolean remove(String key, User user) {
        if (collection.containsKey(key)) {
            collection.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public int removeAll(Room room, User user) {
        ArrayList<String> s = new ArrayList<>();
        collection.forEach((k, v) -> {
            if (v.isIdentical(room)) {
                s.add(room.getShape());
            }
        });
        for (String value : s) collection.remove(value);
        return s.size();
    }

    @Override
    public int removeAllWithLowerKey(String key, User user) {
        ArrayList<String> s = new ArrayList<>();
        collection.forEach((k, v) -> {
            if (k.compareTo(key) < 0) {
                s.add(k);
            }
        });
        for (String value : s) collection.remove(value);
        return s.size();
    }

    public Map<String, Room> getRooms() {
        return collection;
    }

    @Override
    public Map<String, Room> getRooms(User user) {
        return getRooms();
    }

    @Override
    public boolean add(User user) {
        return true;
    }

    @Override
    public User authenticate(String login, String password) {
        return null;
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public String toString() {
        return "Тип коллекции: " + collection.getClass() +
                "\nТип элементов: " + Room.class +
                "\nДата инициализации: " + createdDate +
                "\nКоличество элементов: " + collection.size();
    }
}
