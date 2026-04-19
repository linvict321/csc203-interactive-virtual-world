public interface Movable {

    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler); //entity
    Point nextPosition(WorldModel world, Point dest); //entity

}
