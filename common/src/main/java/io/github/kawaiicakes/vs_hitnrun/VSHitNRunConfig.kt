package io.github.kawaiicakes.vs_hitnrun

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object VSHitNRunConfig {
    @JvmField
    val SERVER = Server()

    class Server {
        @JsonSchema(
            description = "The minimum collision velocity, measured in (blocks / tick)^2, when this mod's effects should apply. The collision velocity is how much the player was moved as a result of being hit.",
            min = 0.0,
            max = 1.0,
            defaultValue = "0.1487"
        )
        var speedThreshold = 0.1487

        @JsonSchema(
            description = "The minimum mass of a ship for which this mod's effects should apply",
            min = 0.0,
            defaultValue = "0.5"
        )
        var massThreshold = 0.5

        @JsonSchema(
            description = "Damage is calculated by the formula damageCoefficient * (1/2)(ship mass)(velocity)^2, where velocity is how much the entity was moved as a result of being hit.",
            min = 0.0,
            max = 1.0,
            defaultValue = "0.00001122"
        )
        var damageCoefficient = 0.00001122

        @JsonSchema(
            description = "Minimum damage that can be inflicted by a ship.",
            min = 0.0,
            defaultValue = "0.5"
        )
        var minDamage = 0.5

        @JsonSchema(
            description = "Maximum damage that can be inflicted by a ship.",
            min = 0.0,
            defaultValue = "~1.797E+308"
        )
        var maxDamage = Double.MAX_VALUE

        @JsonSchema(
            description = "Knockback is calculated by the formula knockbackCoefficient * (1/2)(ship mass)(velocity)^2, where velocity is how much the player was moved as a result of being hit.",
            min = 0.0,
            max = 1.0,
            defaultValue = "0.000003174"
        )
        var knockbackCoefficient = 0.000003174

        @JsonSchema(
            description = "Minimum knockback allowed. Number is roughly equivalent to knockback enchantment.",
            min = 0.0,
            defaultValue = "1.0"
        )
        var minKnockback = 1.0

        @JsonSchema(
            description = "Maximum knockback allowed. Number is roughly equivalent to knockback enchantment.",
            min = 0.0,
            defaultValue = "5.0"
        )
        var maxKnockback = 5.0

        @JsonSchema(description = "Damage multiplier for falling objects.", min = 0.0, defaultValue = "5.6")
        var crushingMultiplier = 3.2

        @JsonSchema(description = "If the entity's velocity contributes less than this percentage towards a collision velocity, damage is calculated.", min = 0.0, max = 1.0, defaultValue = "0.5")
        var entityVelocityThreshold = 0.5
    }
}

