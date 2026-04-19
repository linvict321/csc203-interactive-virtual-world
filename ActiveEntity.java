import processing.core.PImage;

import java.util.List;

public abstract class ActiveEntity extends AnimatedEntity {

    protected final double actionPeriod;

    protected ActiveEntity(String id, Point position, List<PImage> imgs, double animationPeriod, double actionPeriod) {
        super(id, position, imgs, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public double getActionPeriod() {
        return actionPeriod;
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    public void scheduleActivity(EventScheduler scheduler, WorldModel world, ImageStore img) {
        scheduler.scheduleEvent(this, new Active(this, world, img), actionPeriod);
    }

    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore images) {
        scheduleActivity(scheduler, world, images);
        scheduleAnimation(scheduler, world, images);
    }
}
