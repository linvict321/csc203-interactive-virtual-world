import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairy extends ActiveEntity implements Movable{

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD_IDX = 0;
    public static final int FAIRY_ACTION_PERIOD_IDX = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    //proj3:
    private final PathingStrategy pathing = new AStarPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images,
                 double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public static Fairy createFairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        return new Fairy(id, position, images, animationPeriod, actionPeriod);
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        /*proj2
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = getPosition();
            }
        }

        return newPos;*/

        //proj 3
        Predicate<Point> canPassThrough = point -> !world.isOccupied(point) && world.withinBounds(point); //if its not occupied & is within bounds

        BiPredicate<Point, Point> withinReach = (point, point2) -> point.adjacent(point2) ; //check adjacency

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS; //given strategy

        List<Point> path = pathing.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        return path.isEmpty() ? this.getPosition() : path.get(0);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

//    public void scheduleActions(EventScheduler s, WorldModel w, ImageStore i) {
//        scheduleActivity(s, w, i);
//        scheduleAnimation(s, w, i);
//    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        Optional<Entity> fairyTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = Sapling.createSapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos,
                        imageStore.getImageList(Sapling.SAPLING_KEY), 0);

                world.addEntity(sapling);
                sapling.scheduleActivity(scheduler, world, imageStore);
            }
        }
        scheduleAction(scheduler, world, imageStore);

    }

}