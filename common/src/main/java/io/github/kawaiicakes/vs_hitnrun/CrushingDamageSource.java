package io.github.kawaiicakes.vs_hitnrun;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrushingDamageSource extends DamageSource {
    private final float mass;

    public CrushingDamageSource(Holder<DamageType> holder, float mass) {
        super(holder);
        this.mass = mass;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
        // TODO (1.1.a)
        final String crushed = this.mass >= 3000 ? "heavy" : "light";

        return Component.translatable("death.attack.vs_hitnrun.crushed." + crushed, livingEntity.getDisplayName());
    }
}
