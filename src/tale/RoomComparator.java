package tale;

import java.util.Comparator;

public class RoomComparator implements Comparator<Room> {
    @Override
    public int compare(Room room1, Room room2) {
        return room1.getShape().toUpperCase().compareTo(room2.getShape().toUpperCase());
    }
}