import processing.core.PImage;

import java.util.List;

public abstract class AnimatedEntity extends Entity{ //abstract class for all animation related functions

    protected final double animationPeriod;

    public AnimatedEntity(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public void scheduleAnimation(EventScheduler scheduler, WorldModel world,  ImageStore images) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    public double getAnimationPeriod() { return animationPeriod; }

}
