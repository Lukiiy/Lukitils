package me.lukiiy.utils.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoat.class)
public abstract class BoatMix {
    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void a(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof AbstractBoat) cir.setReturnValue(false);
    }

    @Inject(method = "push", at = @At("HEAD"), cancellable = true)
    private void b(Entity entity, CallbackInfo ci) {
        if (entity instanceof AbstractBoat) ci.cancel();
    }
}
