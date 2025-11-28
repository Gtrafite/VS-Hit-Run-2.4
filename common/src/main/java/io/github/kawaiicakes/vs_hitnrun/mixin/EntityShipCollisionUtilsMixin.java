package io.github.kawaiicakes.vs_hitnrun.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

// priority must be high as other VS addons (namely VLib) are known to mixin to here
@Mixin(value = EntityShipCollisionUtils.class, priority = 4201337)
public abstract class EntityShipCollisionUtilsMixin {
    @SuppressWarnings("RedundantCast")
    @WrapMethod(method = "adjustEntityMovementForShipCollisions")
    private Vec3 wrapCollision(
            Entity entity, Vec3 movement, AABB entityBoundingBox, Level world,
            Operation<Vec3> original
    ) {
        final Vec3 result = original.call(entity, movement, entityBoundingBox, world);
        if (!(world instanceof ServerLevel serverLevel)) return result;

        final EntityDraggingInformation dragInfo = ((IEntityDraggingInformationProvider) (Object) entity).getDraggingInformation();

        final Vec3 difference = result.subtract(movement);
        final double differenceMagnitudeSqr = difference.lengthSqr();
        // TODO - config value
        if (dragInfo.getLastShipStoodOn() != null && differenceMagnitudeSqr > 0.1269) {
            ((Roadkillable) (Object) entity).vs_hitnrun$onRoadkill(
                    serverLevel,
                    difference, differenceMagnitudeSqr,
                    dragInfo
            );
        }

        return result;
    }
}
