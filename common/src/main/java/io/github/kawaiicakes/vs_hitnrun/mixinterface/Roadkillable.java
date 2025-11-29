package io.github.kawaiicakes.vs_hitnrun.mixinterface;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO - if this proves problematic, this could be replaced with an "on contact" interface instead, whereby previous pos and current pos are compared...
/**
 * Implementations define what happens when hit by a ship.
 */
@ParametersAreNonnullByDefault
public interface Roadkillable {
    /**
     * @param deltaV {@link Vec3} indicating the change in velocity on the player over one tick as a result of the collision.
     * @param deltaVMagnitudeSqr {@code double} as magnitude of delta V squared. Passed so this does not need to be calculated from
     *                           {@code deltaV} again. Measured in meters^2 per tick^2.
     * @param mass  Ship mass as a {@code double}
     * @param info {@link EntityDraggingInformation} attached to this
     */
    default void vs_hitnrun$onRoadkill(
            ServerLevel level, Vec3 deltaV, double deltaVMagnitudeSqr, double mass, EntityDraggingInformation info
    )
    {}
}
