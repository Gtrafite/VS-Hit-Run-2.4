<div align="center">
___

### Collision damage between players and VS ships.

![Mod Loader](https://img.shields.io/badge/mod_loader-fabric_|_forge-ffe8e9?style=for-the-badge&labelColor=ffced2)
![Environment](https://img.shields.io/badge/environment-client_|_server-ffe8e9?style=for-the-badge&labelColor=ffced2)
[![Bug Reports](https://img.shields.io/github/issues/kawaiicakes/Vehicular-manSlaughter?style=for-the-badge&logo=github&labelColor=ffe8e9&color=ffced2)](https://github.com/kawaiicakes/Vehicular-manSlaughter/issues)

[![Modrinth](https://img.shields.io/modrinth/dt/1hIzZIwF?style=for-the-badge&logo=modrinth&labelColor=ffceea&color=ffe8f5)](https://modrinth.com/project/vs-hit-run)
## THIS MOD IS IN BETA!
</div>

Highly-configurable Valkyrien Skies 2 addon that lets ships do damage when colliding with entities. It's that simple! 
Horrific acts of **V**ehicular man**S**laughter (in Minecraft) await!

---
# 📖 Information
Ships do damage upon hitting players depending on the player's velocity, the ship's velocity and the ship's mass.
Config options are described below, don't touch them unless you know what you are doing!
## Config Options
- `speedThreshold` - The minimum collision velocity, measured in (blocks / tick)^2, when this mod's effects should apply. The collision velocity is how much the player was moved as a result of being hit.
- `massThreshold` - The minimum mass of a ship for which this mod's effects should apply.
- `damageCoefficient` - Damage is calculated by the formula damageCoefficient * (1/2)(ship mass)(velocity)^2, where velocity is how much the entity was moved as a result of being hit.
- `minDamage` - Minimum damage that can be inflicted by a ship.
- `maxDamage` - Maximum damage that can be inflicted by a ship.
- `knockbackCoefficient` - Knockback is calculated by the formula knockbackCoefficient * (1/2)(ship mass)(velocity)^2, where velocity is how much the player was moved as a result of being hit.
- `minKnockback` - Minimum knockback allowed. Number is roughly equivalent to knockback enchantment.
- `maxKnockback` - Maximum knockback allowed. Number is roughly equivalent to knockback enchantment.
- `crushingMultiplier` - Damage multiplier for falling objects.
- `entityVelocityThreshold` - If the entity's velocity contributes less than this percentage towards a collision velocity, damage is calculated.
- `heavyMass` - The mass over which death messages will consider the ship heavy
- `fastShip` - The speed over which death messages will consider the ship fast (in (m/t)^2)

# ⌛ Planned Features
- High-G maneuvers (like crashing or pulling out of a steep dive) can cause ships to tear themselves apart
    - Blocks will receive definable "tensile strength" property
    - Configurable forces
- Seated entities take damage and can get flung during collisions, riding entities get flung