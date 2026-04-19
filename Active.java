import processing.core.PImage;

import java.util.List;

/**
 * An action that can be taken by a particular Entity.
 * There are two types of actions in this World:
 * - Activity actions: things like the Sapling growing up, or the DudeNotFull finding a
 *      Tree or Sapling to cut down, or the Fairy finding a Stump to turn into a Sapling.
 * - Animation actions: things like the Dude swinging his axe, or the Tree swaying, or
 *      the Fairy twinkling.
 */
public class Active implements Action {
    private final Entity kind;
    private final WorldModel world;
    private final ImageStore img;

    public Active(Entity kind, WorldModel world, ImageStore img) {
        this.kind = kind;
        this.world = world;
        this.img = img;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        if (kind instanceof ActiveEntity ae) {
            ae.executeActivity(world, img, scheduler);
        }
    }

}
