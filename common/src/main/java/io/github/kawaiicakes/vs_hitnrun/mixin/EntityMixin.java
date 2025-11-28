package io.github.kawaiicakes.vs_hitnrun.mixin;

import com.mojang.logging.LogUtils;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(Entity.class)
public abstract class EntityMixin implements Roadkillable {
    @Shadow public abstract float getYRot();
    @Shadow public abstract void push(double x, double y, double z);
    @Shadow public abstract double getY();
    @Shadow public abstract double getY(double scale);
    @Shadow public abstract double getX();
    @Shadow public abstract double getZ();
    @Shadow public abstract SoundSource getSoundSource();
    @Shadow public abstract boolean hurt(DamageSource source, float amount);
    @Shadow public abstract DamageSources damageSources();

    @Override
    @ParametersAreNonnullByDefault
    public void vs_hitnrun$onRoadkill(
            ServerLevel serverLevel, Vec3 deltaV, double deltaVMagnitudeSqr, EntityDraggingInformation info
    ) {
        final double convertedSpeed = Math.sqrt(deltaVMagnitudeSqr) * 20;
        LogUtils.getLogger().info("Bonked! {} m/s", convertedSpeed);

        // TODO - take into account config and mass. Using the speed like this is a debug convenience
        final double thresholdSpeed = (convertedSpeed / 7.61);

        // FIXME - for a spinning object, the added movement can sometimes be different to what one
        //  would expect as it's simply just the expected future position of an entity while being dragged.
        //  this causes knockback to sometimes be applied in a direction opposite to what is expected
        final Vec3 added = VectorConversionsMCKt.toMinecraft(info.getAddedMovementLastTick());
        final Vec2 normalizedDeltaV = new Vec2((float) added.x, (float) added.z).normalized();
        final float yRotFromDeltaV = (float) Mth.atan2(normalizedDeltaV.y, normalizedDeltaV.x);
        this.push(
                -Mth.sin(yRotFromDeltaV * ((float)Math.PI / 180)) * thresholdSpeed * 1.2,
                0.1,
                Mth.cos(yRotFromDeltaV * ((float)Math.PI / 180)) * thresholdSpeed * 1.2
        );

        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(),
                (float) thresholdSpeed, 1.0f
        );

        // TODO - custom damage source, damage calculation
        this.hurt(this.damageSources().fall(), (float) (thresholdSpeed * 3));

        serverLevel.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                this.getX(), this.getY(0.5), this.getZ(),
                (int) Math.ceil(thresholdSpeed * 2),
                0.1, 0.0, 0.1, 0.2
        );
    }
}
