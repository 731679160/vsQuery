package dataowner;

import java.util.List;

public class SpatialData {
    public int x;
    public int y;
    int id;

    public SpatialData(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public SpatialData(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
