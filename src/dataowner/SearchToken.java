package dataowner;

import util.Utils;

import java.io.Serializable;
import java.util.List;

public class SearchToken implements Serializable {
    public List<String> zeroPrefix_x;
    public List<String> zeroPrefix_y;
    public List<String> onePrefix_x;
    public List<String> onePrefix_y;

    public SearchToken(List<String> zeroPrefix_x, List<String> zeroPrefix_y, List<String> onePrefix_x, List<String> onePrefix_y) {
        this.zeroPrefix_x = zeroPrefix_x;
        this.zeroPrefix_y = zeroPrefix_y;
        this.onePrefix_x = onePrefix_x;
        this.onePrefix_y = onePrefix_y;
    }

    public List<String> getByIndex(int tag) {
        switch (tag) {
            case 1: return zeroPrefix_x;
            case 2: return zeroPrefix_y;
            case 3: return onePrefix_x;
            case 4: return onePrefix_y;
            default: return null;
        }
    }
}
