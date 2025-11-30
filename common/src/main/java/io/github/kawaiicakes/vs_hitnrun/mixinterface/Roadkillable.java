package io.github.kawaiicakes.vs_hitnrun.mixinterface;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.apigame.collision.ConvexPolygonc;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

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
    static @NotNull Vec3 calculateProperCollisionVelocity(
            List<ConvexPolygonc> collidingPolygons, Vec3 entityMovement,
            Vec3 shipCenterOfMassInWorld, Vector3dc shipVelocity, Vector3dc shipOmega
    ) {
        // TODO - test and fix as needed
        int polygonCount = 0;

        Vec3 velocity = VectorConversionsMCKt.toMinecraft(shipVelocity);
        Vec3 omega = VectorConversionsMCKt.toMinecraft(shipOmega);

        Vec3 averagedCollisionCenter = new Vec3(0, 0, 0);
        Vec3 averagedCollisionNormal = new Vec3(0, 0, 0);

        for (ConvexPolygonc polygon : collidingPolygons) {
            polygonCount++;

            Vector3d centerOfPolygon = new Vector3d();
            for (Vector3dc point : polygon.getPoints()) {
                centerOfPolygon.add(point, centerOfPolygon);
            }
            centerOfPolygon.div(8);
            averagedCollisionCenter = averagedCollisionCenter.add(VectorConversionsMCKt.toMinecraft(centerOfPolygon));

            Vector3d normalToPolygonCenter = new Vector3d();
            for (Vector3dc point : polygon.getNormals()) {
                normalToPolygonCenter.add(point, normalToPolygonCenter);
            }
            normalToPolygonCenter.normalize();
            averagedCollisionNormal = averagedCollisionNormal.add(VectorConversionsMCKt.toMinecraft(normalToPolygonCenter));
        }

        averagedCollisionCenter = averagedCollisionCenter.scale((double) 1 / polygonCount);
        averagedCollisionNormal = averagedCollisionNormal.scale((double) 1 / polygonCount);

        final Vec3 displacementFromCenterOfMass = averagedCollisionCenter.subtract(shipCenterOfMassInWorld);
        final Vec3 totalShipVelocity = omega.cross(displacementFromCenterOfMass).add(velocity);

        final Vec3 collisionVelocityRaw = totalShipVelocity.subtract(entityMovement);

        return collisionVelocityRaw.scale(collisionVelocityRaw.dot(averagedCollisionNormal));
    }
}
