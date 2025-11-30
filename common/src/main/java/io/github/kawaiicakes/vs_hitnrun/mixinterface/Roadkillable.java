package io.github.kawaiicakes.vs_hitnrun.mixinterface;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.valkyrienskies.core.apigame.collision.ConvexPolygonc;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Implementations define what happens when hit by a ship.
 */
@ParametersAreNonnullByDefault
public interface Roadkillable {
    /**
     * @param deltaV {@link Vec3} indicating the change in velocity on the player over one tick as a result of the collision.
     * @param mass  Ship mass as a {@code double}
     * @param info {@link EntityDraggingInformation} attached to this
     */
    default void vs_hitnrun$onRoadkill(
            ServerLevel level, Vec3 deltaV, double mass, EntityDraggingInformation info
    )
    {}

    /**
     * The original collision velocity is actually wildly inaccurate. We recalculate it here more rigorously.
     * @param collidingPolygons a list of all the ship polygons colliding with the entity
     * @param entityMovement the velocity of the entity
     * @param shipCenterOfMassInWorld the position of the ship's center of mass
     * @param shipVelocity the velocity of the ship
     * @param shipOmega the angular velocity of the ship
     * @return A more accurate velocity representing the sum of the ship and entity's velocity at the point of contact.
     */
    static Vec3 calculateProperCollisionVelocity(
            List<ConvexPolygonc> collidingPolygons, Vec3 entityMovement,
            Vec3 shipCenterOfMassInWorld, Vector3dc shipVelocity, Vector3dc shipOmega
    ) {
        // TODO
        return null;
    }
}
