package io.github.kawaiicakes.vs_hitnrun;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RammingDamageSource extends DamageSource {
    private final Vec3 deltaV;
    private final float mass;

    public RammingDamageSource(Holder<DamageType> holder, Vec3 deltaV, float mass) {
        super(holder);
        this.deltaV = deltaV;
        this.mass = mass;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
        final String heavy = this.mass >= VSHitNRunConfig.SERVER.getHeavyMass() ? "heavy" : "light";
        final String fast = this.deltaV.lengthSqr() >= VSHitNRunConfig.SERVER.getFastShip() ? "fast" : "slow";

        return Component.translatable(
                "death.attack.vs_hitnrun.rammed." + heavy + "." + fast,
                livingEntity.getDisplayName()
        );
    }
}
