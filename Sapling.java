import processing.core.PImage;

import java.util.List;

public class Sapling extends ActiveEntity{

    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000;
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH_IDX = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;

    private int health;
//    private final int healthLimit = ;

    public Sapling(String id, Point position, List<PImage> images, int health) {
        super(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD);
        this.health = health;
//        this.healthLimit = healthLimit;
    }

    public static Sapling createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(id, position, images, health);
    }
//    createSapling(SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageStore.getImageList(SAPLING_KEY), 0);
//
//                world.addEntity(sapling);
//                sapling.scheduleActions(scheduler, world, imageStore);

    @Override
    public void nextImage() {
        if (getHealth() <= 0) {
            setImageIndex(0);
        } else if (getHealth() < SAPLING_HEALTH_LIMIT) {
            setImageIndex(getImages().size() * getHealth() / SAPLING_HEALTH_LIMIT);
        } else {
            setImageIndex(getImages().size() - 1);
        }
    }

    public boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getHealth() <= 0) {
            Entity stump = Stump.createStump(Stump.STUMP_KEY + "_" + getId(), getPosition(),
                    imageStore.getImageList(Stump.STUMP_KEY));

            world.removeEntity(scheduler, stump);
            world.addEntity(stump);

            return true;

        } else if (getHealth() >= SAPLING_HEALTH_LIMIT) {
            ActiveEntity tree = Tree.createTree(Tree.TREE_KEY + "_" + getId(), getPosition(),
                    Functions.getNumFromRange(Tree.TREE_ACTION_MAX, Tree.TREE_ACTION_MIN),
                    Functions.getNumFromRange(Tree.TREE_ANIMATION_MAX, Tree.TREE_ANIMATION_MIN),
                    Functions.getIntFromRange(Tree.TREE_HEALTH_MAX, Tree.TREE_HEALTH_MIN),
                    imageStore.getImageList(Tree.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleAction(scheduler, world, imageStore);

            return true;
        }
        return false;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { //from entity executeSaplingActivity
        this.health++;
        if (!transformSapling(world, scheduler, imageStore)) {
            scheduleActivity(scheduler, world, imageStore);
        }
    }

    public void setHealth(int amount) { this.health = amount; }

    public int getHealth() {
        return health; }
}
