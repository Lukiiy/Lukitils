package me.lukiiy.utils.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Snowball.class)
public abstract class SnowballMix extends ThrowableItemProjectile {
    protected SnowballMix(EntityType<? extends Snowball> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        Snowball self = (Snowball) (Entity) this;
        Entity owner = self.getOwner();

        LivingEntity nearest = null;
        double nearestDist = 32 * 32;

        for (LivingEntity target : self.level().getEntitiesOfClass(LivingEntity.class, self.getBoundingBox().inflate(32))) {
            if (!target.isAlive()) continue;
            if (target == owner) continue;

            double distance = self.distanceToSqr(target);

            if (distance < nearestDist) {
                nearestDist = distance;
                nearest = target;
            }
        }

        if (nearest == null) return;

        Vec3 velocity = self.getDeltaMovement();
        double speed = velocity.length();
        if (speed < .001) return;

        Vec3 target = nearest.position().add(0, nearest.getBbHeight() * .5, 0);
        Vec3 dir = target.subtract(self.position()).normalize();

        self.setDeltaMovement(velocity.normalize().scale(.8).add(dir.scale(.2)).normalize().scale(speed));
        self.needsSync = true;
    }
}
