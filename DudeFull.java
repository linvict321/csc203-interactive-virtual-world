import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DudeFull extends ActiveEntity implements Movable {
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD_IDX = 0;
    public static final int DUDE_ANIMATION_PERIOD_IDX = 1;
    public static final int DUDE_RESOURCE_LIMIT_IDX = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    private final PathingStrategy pathing = new AStarPathingStrategy();

    //    this.health = health;
//    this.healthLimit = healthLimit;
    int resourceLimit;
    int resourceCount;

    public DudeFull(String id, Point position, List<PImage> images,
                double actionPeriod, double animationPeriod,
                    int resourceLimit){
        super(id, position, images, actionPeriod, animationPeriod);
//        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    public int getResourceCount(){
        return resourceCount;
    }
    public int  getResourceLimit(){
        return resourceLimit;
    }
    /**
     * Creates a new DudeFUll
     * @param id The Dude's id.
     *           If a DudeNotFull turns into a DudeFull, it will still have the same id.
     * @param position The Dude's x,y position in the World.
     * @param actionPeriod The time (seconds) taken for each activity (going to the House and turning into a DudeNotFull).
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param resourceLimit The amount of wood this Dude can carry.
     * @param images Images to use for the Dude.
     * @return a new Entity whose type is DudeFull.
     */
    public static DudeFull createDudeFull(String id, Point position, double animationPeriod, double actionPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, actionPeriod, animationPeriod, resourceLimit);
    }
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
//            if (target instanceof Tree tree)
//                tree.setHealth(tree.getHealth() - 1);
//            else if (target instanceof Sapling sapling)
//                sapling.setHealth(sapling.getHealth() - 1);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        /*int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
                newPos = getPosition();
            }
        }
        return newPos; */

        //proj3
        Predicate<Point> canPassThrough = p->world.withinBounds(p) &&
                (!world.isOccupied(p) || world.getOccupancyCell(p) instanceof Stump); //if its not occupied & is within bounds

        BiPredicate<Point, Point> withinReach = (p1, point2) -> p1.adjacent(point2) ; //check adjacency //check adjacency

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS; //given strategy

        List<Point> path = pathing.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        return path.isEmpty() ? this.getPosition() : path.get(0);

    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = DudeNotFull.createDudeNotFull(getId(), getPosition(), getActionPeriod(), getAnimationPeriod(),
                getResourceLimit(), getImages());
        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleAction(scheduler, world, imageStore);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(House.class)));
        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduleActivity(scheduler, world, imageStore);
        }

    }

}
