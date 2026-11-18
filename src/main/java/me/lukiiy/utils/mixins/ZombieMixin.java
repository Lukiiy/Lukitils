package me.lukiiy.utils.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.lukiiy.utils.Lukitils;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {
    @ModifyReturnValue(method = "getAmbientSound", at = @At("RETURN"), remap = false)
    private SoundEvent a(final SoundEvent original) {
        return SoundEvents.CHICKEN_EGG;
    }

    @ModifyReturnValue(method = "getHurtSound", at = @At("RETURN"), remap = false)
    private SoundEvent b(final SoundEvent original) {
        return SoundEvents.CHICKEN_EGG;
    }

    @ModifyReturnValue(method = "getDeathSound", at = @At("RETURN"), remap = false)
    private SoundEvent c(final SoundEvent original) {
        return SoundEvents.CHICKEN_EGG;
    }

    @ModifyReturnValue(method = "getStepSound", at = @At("RETURN"), remap = false)
    private SoundEvent d(final SoundEvent original) {
        return SoundEvents.CHICKEN_EGG;
    }
}
