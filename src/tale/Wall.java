package tale;

import java.io.Serializable;

public class Wall implements Serializable {
    private String material;

    public Wall(String material) {
        this.material = material;
    }

    void destroyedWall(Room room) {
        if (room == null) System.out.println();
        else {
            if (room.getFloor() > 12) System.out.println("Некоторые стены были разрушены");
            else if ((room.getFloor() < 12) && (room.getFloor() > 5))
                System.out.println("Стены выглядели достаточно ветхими");
            else System.out.println("Стены выглядели хорошо сохранившимися");
        }
    }

    @Override
    public String toString() {
        return "walls {" +
                ", material= " + getMaterial() +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Wall wall = (Wall) obj;
        if (material != wall.material) return false;
        boolean c = obj.hashCode()== this.hashCode() ? true : false;
        return c;
    }

    public String getMaterial() {
        return material;
    }
}
