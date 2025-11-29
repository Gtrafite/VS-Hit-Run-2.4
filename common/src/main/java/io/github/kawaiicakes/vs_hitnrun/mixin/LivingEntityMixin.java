package io.github.kawaiicakes.vs_hitnrun.mixin;

import io.github.kawaiicakes.vs_hitnrun.VSHitNRun;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Roadkillable {
    @Shadow public abstract void knockback(double strength, double x, double z);

    @Shadow public abstract float getHealth();

    @Override
    @ParametersAreNonnullByDefault
    public void vs_hitnrun$onRoadkill(
            ServerLevel serverLevel, Vec3 deltaV, double deltaVMagnitudeSqr, EntityDraggingInformation info
    ) {
        final double convertedSpeed = Math.sqrt(deltaVMagnitudeSqr) * 20;
        // TODO - (1.1.c)
        final double thresholdSpeed = (convertedSpeed / 7.61);

        // FIXME - (1.1.a)
        final Vec3 added = VectorConversionsMCKt.toMinecraft(info.getAddedMovementLastTick());
        final Vec2 normalizedDeltaV = new Vec2((float) added.x, (float) added.z).normalized();
        final float yRotFromDeltaV = (float) Mth.atan2(normalizedDeltaV.y, normalizedDeltaV.x);
        this.knockback(
                thresholdSpeed * 1.2f,
                Mth.sin(yRotFromDeltaV * ((float)Math.PI / 180)),
                -Mth.cos(yRotFromDeltaV * ((float)Math.PI / 180))
        );

        //noinspection DataFlowIssue
        final double mass = ((ServerShip) VSGameUtilsKt.getAllShips(serverLevel).getById(info.getLastShipStoodOn()))
                .getInertiaData()
                .getMass();

        final float oldHealth = this.getHealth();
        final Supplier<DamageSource> function = added.horizontalDistanceSqr() < added.y * added.y
                ? () -> VSHitNRun.crushed(this.damageSources(), (float) mass)
                : () -> VSHitNRun.rammed(this.damageSources(), added, (float) mass);
        // TODO - (1.1.a)
        final boolean wasHurt = this.hurt(
                function.get(),
                (float) (thresholdSpeed * 3)
        );
        final float newHealth = this.getHealth();

        if (!wasHurt) {
            serverLevel.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(),
                    (float) thresholdSpeed, 1.0f
            );

            return;
        }

        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(),
                (float) thresholdSpeed, 1.0f
        );

        serverLevel.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                this.getX(), this.getY(0.5), this.getZ(),
                (int) (oldHealth - newHealth) / 2,
                0.1, 0.0, 0.1, 0.2
        );
    }

    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
