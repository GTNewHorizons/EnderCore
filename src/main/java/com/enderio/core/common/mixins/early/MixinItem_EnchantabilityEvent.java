package com.enderio.core.common.mixins.early;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.enderio.core.common.event.ItemStackEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(Item.class)
public class MixinItem_EnchantabilityEvent {

    @ModifyReturnValue(
            method = "getItemEnchantability(Lnet/minecraft/item/ItemStack;)I",
            at = @At("RETURN"),
            remap = false)
    private int fireItemEnchantabilityEvent(int original, ItemStack stack) {
        ItemStackEvent.ItemEnchantabilityEvent event = new ItemStackEvent.ItemEnchantabilityEvent(stack, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.enchantability;
    }
}
