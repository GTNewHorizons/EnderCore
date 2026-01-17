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
                if (!ToolUtil.canDoEnergyOperations(tool)) return 1;
                else if (((ItemSickleRF) item).isEmpowered(tool)) return 5;
            }

            if (isCoFHCoreLoaded && item instanceof ItemSickleAdv) return ((ItemSickleAdv) item).radius;
        }
        return 0;
    }

    public static void damageDurability(ItemStack tool, int cropsHarvested, EntityPlayer player) {
        if (ToolUtil.usesEnergy(tool, cropsHarvested)) return;

        if (tool.getItem() instanceof ItemTool) tool.damageItem(cropsHarvested, player);
    }

    public static boolean canDoEnergyOperations(ItemStack stack) {
        if (ToolUtil.isRedstoneArsenalLoaded && stack.getItem() instanceof ItemSickleRF) {
            ItemSickleRF sickle = (ItemSickleRF) stack.getItem();

            boolean isEmpowered = sickle.isEmpowered(stack);
            int unbreakingLevel = MathHelper
                    .clamp((EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)), 0, 4);
            int costRF = sickle.energyPerUse * (5 - unbreakingLevel) / 5;

            if (sickle.getEnergyStored(stack) > costRF) {
                sickle.extractEnergy(
                        stack,
                        isEmpowered ? sickle.energyPerUseCharged * (5 - unbreakingLevel) / 5
                                : sickle.energyPerUse * (5 - unbreakingLevel) / 5,
                        false);
                return true;
            }
        }

        return false;
    }

    public static boolean usesEnergy(ItemStack tool, int cropsHarvested) {
        if (ToolUtil.isRedstoneArsenalLoaded && tool.getItem() instanceof ItemSickleRF) {
            ItemSickleRF RFTool = (ItemSickleRF) tool.getItem();
            int unbreakingLvl = MathHelper
                    .clamp(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, tool), 0, 4);
            int RFCost = RFTool.isEmpowered(tool) ? RFTool.energyPerUseCharged * (5 - unbreakingLvl) / 5
                    : RFTool.energyPerUse * (5 - unbreakingLvl) / 5;

            RFTool.extractEnergy(tool, RFCost, false);
            return true;
        }
        return false;
    }
}
