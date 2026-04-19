import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DudeNotFull extends ActiveEntity implements Movable{

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD_IDX = 0;
    public static final int DUDE_ANIMATION_PERIOD_IDX = 1;
    public static final int DUDE_RESOURCE_LIMIT_IDX = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    private final PathingStrategy pathing = new AStarPathingStrategy();

    private final int resourceLimit;
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images,
                      double actionPeriod, double animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;

    }

    public int getResourceCount(){
        return resourceCount;
    }
    public int  getResourceLimit(){
        return resourceLimit;
    }

    public static DudeNotFull createDudeNotFull(String id, Point pos,
                                                double animationPeriod, double actionPeriod,
                                                int resourceLimit, List<PImage> imgs) {
        return new DudeNotFull(id, pos, imgs, actionPeriod, animationPeriod, resourceLimit);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {

        if (getPosition().adjacent(target.getPosition())) {
            if (target instanceof Tree tree)
                tree.setHealth(tree.getHealth() - 1);
            else if (target instanceof Sapling sapling)
                sapling.setHealth(sapling.getHealth() - 1);
            this.resourceCount += 1;
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                if (world.isOccupied(nextPos) && world.getOccupancyCell(nextPos) instanceof Stump) { //for the stump
                    Entity stump = (Entity) world.getOccupancyCell(nextPos);
                    world.removeEntity(scheduler, stump); //remove stump
                }
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }

    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        /* proj2
        int horiz = Integer.signum(dest.x - getPosition().x);

        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || (world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump))) {
            int vert = Integer.signum(dest.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || (world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump))) {
                newPos = getPosition();
            }
        }
        return newPos;
        */
        //proj 3
        Predicate<Point> canPassThrough = p->world.withinBounds(p) &&
                        (!world.isOccupied(p) || world.getOccupancyCell(p) instanceof Stump); //if its not occupied & is within bounds

        BiPredicate<Point, Point> withinReach = (p1, point2) -> p1.adjacent(point2) ; //check adjacency

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS; //given strategy

        List<Point> path = pathing.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        return path.isEmpty() ? this.getPosition() : path.get(0);


    }

    private boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getResourceCount() >= getResourceLimit()) {
            DudeFull dude = DudeFull.createDudeFull(getId(), getPosition(), getActionPeriod(),
                    getAnimationPeriod(), getResourceLimit(), getImages());
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleAction(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), new ArrayList<>(List.of(Tree.class, Sapling.class)));

        if (target.isEmpty()
                || !this.moveTo(world, target.get(), scheduler)
                || !transformNotFull(world, scheduler, imageStore)) {
            scheduleActivity(scheduler, world, imageStore);
        }

    }
}
