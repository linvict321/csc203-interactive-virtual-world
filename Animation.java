public final class Animation implements Action{
    //for the animation stuff , taken from event scheduler

    private final Entity entity;
    private final int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        if (entity instanceof AnimatedEntity entity1) {
            entity1.nextImage();
            if (repeatCount != 1) {
                scheduler.scheduleEvent(entity, new Animation(this.entity, Math.max(this.repeatCount - 1, 0)), entity1.getAnimationPeriod());
            }
        }
    }
}
