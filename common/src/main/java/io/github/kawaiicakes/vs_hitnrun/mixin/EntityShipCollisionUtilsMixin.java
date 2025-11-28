package io.github.kawaiicakes.vs_hitnrun.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

// priority must be high as other VS addons (namely VLib) are known to mixin to here
@Mixin(value = EntityShipCollisionUtils.class, priority = 4201337)
public abstract class EntityShipCollisionUtilsMixin {
    @WrapMethod(method = "adjustEntityMovementForShipCollisions")
    private Vec3 wrapCollision(
            Entity entity, Vec3 movement, AABB entityBoundingBox, Level world,
            Operation<Vec3> original
    ) {
        final Vec3 result = original.call(entity, movement, entityBoundingBox, world);

        if (world.isClientSide) return result;

        Vec3 difference = result.subtract(movement);
        //noinspection RedundantCast
        final boolean standingOnShip = ((IEntityDraggingInformationProvider) (Object) entity)
                .getDraggingInformation()
                .isEntityBeingDraggedByAShip();
        // cancel out weird gravity shit
        if (standingOnShip && !difference.equals(Vec3.ZERO)) difference = difference.subtract(0.0, 0.0784000015257083790798376, 0.0);

        LogUtils.getLogger().info("{}", difference);

        // this value would equate to 1 m/s
        // TODO - replace 0.0025 w/ config value squared
        if (difference.lengthSqr() > 0.0025) {
            // TODO - dmg calculations, sfx, kb
            LogUtils.getLogger().info("Bonk!");
        }

        return result;
    }
}
