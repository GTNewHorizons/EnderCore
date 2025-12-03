package com.enderio.core.common.mixins.early;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.enderio.core.common.event.ArrowUpdateEvent;

@Mixin(EntityArrow.class)
public class MixinEntityArrow_UpdateEvent {

    @Inject(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V", shift = At.Shift.AFTER))
    private void fireUpdateEvent(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ArrowUpdateEvent((EntityArrow) (Object) this));
    }
}
