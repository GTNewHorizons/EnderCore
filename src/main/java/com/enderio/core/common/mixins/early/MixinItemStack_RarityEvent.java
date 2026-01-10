package com.enderio.core.common.mixins.early;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.enderio.core.common.event.ItemStackEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(ItemStack.class)
public class MixinItemStack_RarityEvent {

    @ModifyReturnValue(method = "getRarity", at = @At("RETURN"))
    private EnumRarity fireItemRarityEvent(EnumRarity original) {
        ItemStackEvent.ItemRarityEvent event = new ItemStackEvent.ItemRarityEvent((ItemStack) (Object) this, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.rarity;
    }
}
