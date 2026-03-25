package com.enderio.core.common.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import com.github.elenterius.magianaturalis.item.artifact.SickleItem;

import cofh.core.item.tool.ItemSickleAdv;
import cofh.lib.util.helpers.MathHelper;
import cofh.redstonearsenal.item.tool.ItemSickleRF;
import cpw.mods.fml.common.Loader;

public class ToolUtil {

    public static final boolean isMagiaNaturalisLoaded = Loader.isModLoaded("magianaturalis");
    public static final boolean isRedstoneArsenalLoaded = Loader.isModLoaded("RedstoneArsenal");
    public static final boolean isCoFHCoreLoaded = Loader.isModLoaded("CoFHCore");

    public static int getRange(ItemStack tool) {
        if (tool != null) {
            Item item = tool.getItem();
            int defaultValue = 3;

            if (isMagiaNaturalisLoaded && item instanceof SickleItem) return defaultValue;

            if (isRedstoneArsenalLoaded && item instanceof ItemSickleRF) {
                if (!ToolUtil.canDoEnergyOperations(tool, true)) return 1;
                else if (((ItemSickleRF) item).isEmpowered(tool)) return 5;
            }

            if (isCoFHCoreLoaded && item instanceof ItemSickleAdv) return ((ItemSickleAdv) item).radius;
        }
        return 0;
    }

    public static boolean damageDurability(ItemStack stack, EntityPlayer player) {
        if (stack != null) {
            Item tool = stack.getItem();
            if (ToolUtil.isRedstoneArsenalLoaded && tool instanceof ItemSickleRF)
                return ToolUtil.canDoEnergyOperations(stack, false);

            if (tool instanceof ItemTool && tool.isDamageable()) {
                stack.damageItem(1, player);
                return true;
            }
        }

        return false;
    }

    public static boolean canDoEnergyOperations(ItemStack stack, boolean simulate) {
        ItemSickleRF sickle = (ItemSickleRF) stack.getItem();

        if (sickle != null) {
            boolean isEmpowered = sickle.isEmpowered(stack);

            int unbreakingLevel = MathHelper
                    .clamp((EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)), 0, 4);

            int costRF = (isEmpowered ? sickle.energyPerUseCharged : sickle.energyPerUse) * (5 - unbreakingLevel) / 5;

            if (sickle.getEnergyStored(stack) >= costRF) {
                if (!simulate) sickle.extractEnergy(stack, costRF, false);
                return true;
            }
        }

        return false;
    }
}
