package io.github.kawaiicakes.vs_hitnrun.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import io.github.kawaiicakes.vs_hitnrun.VSHitNRunConfig;
import io.github.kawaiicakes.vs_hitnrun.mixinterface.Roadkillable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.collision.ConvexPolygonc;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;

// priority must be high as other VS addons (namely VLib) are known to mixin to here
@Mixin(value = EntityShipCollisionUtils.class, priority = 4201337)
public abstract class EntityShipCollisionUtilsMixin {
    @SuppressWarnings("RedundantCast")
    @Inject(
            method = "adjustEntityMovementForShipCollisions",
            at = @At("RETURN"),
            remap = false
    )
    private void wrapCollision(
            Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, CallbackInfoReturnable<Vec3> cir,
            @Local(name = "collidingShipPolygons") List<ConvexPolygonc> polygons
    ) {
        if (!(world instanceof ServerLevel serverLevel)) return;

        final EntityDraggingInformation dragInfo = ((IEntityDraggingInformationProvider) (Object) entity)
                .getDraggingInformation();

        final Vec3 difference = cir.getReturnValue().subtract(movement);
        final double differenceMagnitudeSqr = difference.lengthSqr();
        if (dragInfo.getLastShipStoodOn() != null && differenceMagnitudeSqr > VSHitNRunConfig.SERVER.getSpeedThreshold()) {
            final ServerShip ship = ((ServerShip) VSGameUtilsKt.getAllShips(serverLevel).getById(
                    dragInfo.getLastShipStoodOn())
            );

            if (ship == null) return;

            final double mass = ship.getInertiaData().getMass();

            if (mass < VSHitNRunConfig.SERVER.getMassThreshold()) return;

            Vec3 properDeltaV = Roadkillable.calculateProperCollisionVelocity(
                    polygons, movement,
                    VectorConversionsMCKt.transformPosition(
                            ship.getTransform().getShipToWorld(),
                            VectorConversionsMCKt.toMinecraft(ship.getInertiaData().getCenterOfMassInShip())
                    ),
                    ship.getVelocity(), ship.getOmega()
            );

            double entityVelocityThreshold = movement.lengthSqr() * VSHitNRunConfig.SERVER.getEntityVelocityThreshold();
            double remainingVelocity = properDeltaV.lengthSqr() * (1 - VSHitNRunConfig.SERVER.getEntityVelocityThreshold());
            if (entityVelocityThreshold >= properDeltaV.lengthSqr() && remainingVelocity < VSHitNRunConfig.SERVER.getSpeedThreshold())
                return;

            ((Roadkillable) (Object) entity).vs_hitnrun$onRoadkill(
                    serverLevel, properDeltaV, mass, dragInfo
            );
        }
    }

    /**
     * Exists for debug purposes. Logged JSON includes points in mathematical format that Desmos will accept
     */
    @Unique
    private static void vs_hitnrun$logPolygons(List<ConvexPolygonc> toReturn, Entity entity, AABB entityBoundingBox) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        JsonArray array = new JsonArray(toReturn.size());
        Vec3 entityPos = entity.getPosition(0);
        array.add("Entity Pos: " + "(" + entityPos.x() + ", " + entityPos.y() + ", " + entityPos.z() + ")");
        array.add("Entity min AABB: " + "(" + entityBoundingBox.minX + ", " + entityBoundingBox.minY + ", " + entityBoundingBox.minZ + ")");
        array.add("Entity max AABB: " + "(" + entityBoundingBox.maxX + ", " + entityBoundingBox.maxY + ", " + entityBoundingBox.maxZ + ")");
        for (ConvexPolygonc polygon : toReturn) {
            JsonObject polygonJson = new JsonObject();

            polygonJson.addProperty("Polygon min AABB","(" + polygon.getAabb().minX() + ", " + polygon.getAabb().minY() + ", " + polygon.getAabb().minZ() + ")");
            polygonJson.addProperty("Polygon max AABB", "(" + polygon.getAabb().maxX() + ", " + polygon.getAabb().maxY() + ", " + polygon.getAabb().maxZ() + ")");

            JsonArray points = new JsonArray();
            for (Vector3dc point : polygon.getPoints()) {
                points.add("(" + point.x() + ", " + point.y() + ", " + point.z() + ")");
            }
            polygonJson.add("points", points);

            JsonArray normals = new JsonArray();
            for (Vector3dc normal : polygon.getNormals()) {
                normals.add("(" + normal.x() + ", " + normal.y() + ", " + normal.z() + ")");
            }
            polygonJson.add("normals", normals);

            array.add(polygonJson);
        }

        LogUtils.getLogger().info("Colliding polygons: {}", gson.toJson(array)
                .replace("\"", "")
        );
    }
}
