package io.github.kawaiicakes.vs_hitnrun.mixin;

import io.github.kawaiicakes.vs_hitnrun.VSHitNRun;
import io.github.kawaiicakes.vs_hitnrun.VSHitNRunConfig;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
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
import java.util.function.Supplier;

@Mixin(Entity.class)
public abstract class EntityMixin implements Roadkillable {
    @Shadow public abstract void push(double x, double y, double z);
    @Shadow public abstract double getY();
    @Shadow public abstract double getX();
    @Shadow public abstract double getZ();
    @Shadow public abstract SoundSource getSoundSource();
    @Shadow public abstract boolean hurt(DamageSource source, float amount);
    @Shadow public abstract DamageSources damageSources();

    @Override
    @ParametersAreNonnullByDefault
    public void vs_hitnrun$onRoadkill(
            ServerLevel serverLevel, Vec3 deltaV, double deltaVMagnitudeSqr, double mass, EntityDraggingInformation info
    ) {
        final double damageCoefficient = VSHitNRunConfig.SERVER.getDamageCoefficient();
        final double minDamage = VSHitNRunConfig.SERVER.getMinDamage();
        final double maxDamage = VSHitNRunConfig.SERVER.getMaxDamage();
        final double knockbackCoefficient = VSHitNRunConfig.SERVER.getKnockbackCoefficient();
        final double minKnockback = VSHitNRunConfig.SERVER.getMinKnockback();
        final double maxKnockback = VSHitNRunConfig.SERVER.getMaxKnockback();
        final double crushingMultiplier = VSHitNRunConfig.SERVER.getCrushingMultiplier();

        // FIXME - (1.0.1.a) for a spinning object, the added movement can sometimes be different to what one
        //  would expect as it's simply just the expected future position of an entity while being dragged.
        //  this causes knockback to sometimes be applied in a direction opposite to what is expected
        final Vec3 added = VectorConversionsMCKt.toMinecraft(info.getAddedMovementLastTick());
        final Vec2 normalizedDeltaV = new Vec2((float) added.x, (float) added.z).normalized();
        final float yRotFromDeltaV = (float) Mth.atan2(normalizedDeltaV.y, normalizedDeltaV.x);
        final double equivalentKnockbackLevel = Mth.clamp(
                knockbackCoefficient * 0.5 * mass * (deltaVMagnitudeSqr * 400),
                minKnockback,
                maxKnockback
        );
        this.push(
                -Mth.sin(yRotFromDeltaV * ((float)Math.PI / 180)) * equivalentKnockbackLevel,
                0.1,
                Mth.cos(yRotFromDeltaV * ((float)Math.PI / 180)) * equivalentKnockbackLevel
        );

        final boolean isCrushing = deltaV.horizontalDistanceSqr() < deltaV.y * deltaV.y;

        final Supplier<DamageSource> function = isCrushing
                ? () -> VSHitNRun.crushed(this.damageSources(), (float) mass)
                : () -> VSHitNRun.rammed(this.damageSources(), deltaV, (float) mass);

        double rawDamage = damageCoefficient * 0.5 * mass * (deltaVMagnitudeSqr * 400);
        if (isCrushing) rawDamage *= crushingMultiplier;
        this.hurt(function.get(), (float) Mth.clamp(
                rawDamage,
                minDamage,
                maxDamage
                )
        );

        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(),
                (float) Mth.clamp(rawDamage / 10, 0.5, 3.0), 1.0f
        );
    }
}
