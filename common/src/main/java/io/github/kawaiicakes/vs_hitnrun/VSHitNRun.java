package io.github.kawaiicakes.vs_hitnrun;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.damagesource.DamageTypes.FLY_INTO_WALL;

public class VSHitNRun {
    public static final String MOD_ID = "vs_hitnrun";

    public static DamageSource crushed(DamageSources instance, float mass) {
        return new CrushingDamageSource(instance.damageTypes.getHolderOrThrow(FLY_INTO_WALL), mass);
    }

    public static DamageSource rammed(DamageSources instance, Vec3 deltaV, float mass) {
        return new RammingDamageSource(instance.damageTypes.getHolderOrThrow(FLY_INTO_WALL), deltaV, mass);
    }


    public static void init() {}

    public static void initClient() {}
}
