import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class House extends Entity{

    public static final String HOUSE_KEY = "house"; //moved to house bc only house uses these two
    public static final int HOUSE_NUM_PROPERTIES = 0;

    public House(String id, Point position, List<PImage> images) {
        super(id, position, images);
    }

    public static House createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

}
