package com.enderio.core.common.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import cofh.lib.util.helpers.MathHelper;
import cofh.redstonearsenal.item.tool.ItemSickleRF;

public class ToolUtil {

    public static boolean canDoEnergyOperations(ItemStack stack) {
        if (stack.getItem() instanceof ItemSickleRF) {
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
        if (tool.getItem() instanceof ItemSickleRF) {
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
