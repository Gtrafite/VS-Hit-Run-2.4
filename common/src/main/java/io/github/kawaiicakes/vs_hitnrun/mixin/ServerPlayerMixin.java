package io.github.kawaiicakes.vs_hitnrun.mixin;

import com.mojang.authlib.GameProfile;
import io.github.kawaiicakes.vs_hitnrun.VSHitNRun;
import io.github.kawaiicakes.vs_hitnrun.VSHitNRunConfig;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements Roadkillable {
    @Shadow public ServerGamePacketListenerImpl connection;

    @Override
    @ParametersAreNonnullByDefault
    public void vs_hitnrun$onRoadkill(
            ServerLevel serverLevel, Vec3 deltaV, double mass, EntityDraggingInformation info
    ) {
        final Vec3 deltaMovement = this.getDeltaMovement();

        final double damageCoefficient = VSHitNRunConfig.SERVER.getDamageCoefficient();
        final double minDamage = VSHitNRunConfig.SERVER.getMinDamage();
        final double maxDamage = VSHitNRunConfig.SERVER.getMaxDamage();
        final double knockbackCoefficient = VSHitNRunConfig.SERVER.getKnockbackCoefficient();
        final double minKnockback = VSHitNRunConfig.SERVER.getMinKnockback();
        final double maxKnockback = VSHitNRunConfig.SERVER.getMaxKnockback();
        final double crushingMultiplier = VSHitNRunConfig.SERVER.getCrushingMultiplier();

        final Vec3 added = VectorConversionsMCKt.toMinecraft(info.getAddedMovementLastTick());
        final Vec2 normalizedDeltaV = new Vec2((float) added.x, (float) added.z).normalized();
        final float yRotFromDeltaV = (float) Mth.atan2(normalizedDeltaV.y, normalizedDeltaV.x);
        final double equivalentKnockbackLevel = Mth.clamp(
                knockbackCoefficient * 0.5 * mass * (deltaV.lengthSqr() * 400),
                minKnockback,
                maxKnockback
        );
        this.knockback(
                equivalentKnockbackLevel * 1.2f,
                Mth.sin(yRotFromDeltaV * ((float)Math.PI / 180)),
                -Mth.cos(yRotFromDeltaV * ((float)Math.PI / 180))
        );

        final float oldHealth = this.getHealth();
        final boolean isCrushing = deltaV.horizontalDistanceSqr() < deltaV.y * deltaV.y;

        final Supplier<DamageSource> function = isCrushing
                ? () -> VSHitNRun.crushed(this.damageSources(), (float) mass)
                : () -> VSHitNRun.rammed(this.damageSources(), deltaV, (float) mass);

        double rawDamage = damageCoefficient * 0.5 * mass * (deltaV.lengthSqr() * 400);
        if (isCrushing) rawDamage *= crushingMultiplier;
        this.hurt(function.get(), (float) Mth.clamp(
                        rawDamage,
                        minDamage,
                        maxDamage
                )
        );
        final float newHealth = this.getHealth();

        if (this.hurtMarked) {
            this.connection.send(new ClientboundSetEntityMotionPacket(this));
            this.hurtMarked = false;
            this.setDeltaMovement(deltaMovement);
        }

        final float healthDiff = oldHealth - newHealth;

        if (healthDiff <= 0) return;

        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(),
                (float) Mth.clamp(rawDamage / 10, 0.5, 3.0), 1.0f
        );

        serverLevel.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                this.getX(), this.getY(0.5), this.getZ(),
                (int) (oldHealth - newHealth) / 2,
                0.1, 0.0, 0.1, Mth.clamp(rawDamage / 20, 0.2, 1.0)
        );
    }

    private ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }
}
