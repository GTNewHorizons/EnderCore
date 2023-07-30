package com.enderio.core.common.handlers;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.common.Handlers.Handler;
import com.enderio.core.common.config.ConfigHandler;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.ToolUtil;
import com.github.elenterius.magianaturalis.item.artifact.SickleItem;
import com.google.common.collect.Lists;

import cofh.core.item.tool.ItemSickleAdv;
import cofh.redstonearsenal.item.tool.ItemSickleRF;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Handler
public class RightClickCropHandler {

    public static class PlantInfo {

        public String seed;
        public String block;
        public int meta = 7;
        public int resetMeta = 0;

        private transient ItemStack seedStack;
        private transient Block blockInst;

        public PlantInfo() {}

        public PlantInfo(String seed, String block, int meta, int resetMeta) {
            this.seed = seed;
            this.block = block;
            this.meta = meta;
            this.resetMeta = resetMeta;
        }

        public void init() {
            seedStack = ItemUtil.parseStringIntoItemStack(seed);
            String[] blockinfo = block.split(":");
            blockInst = GameRegistry.findBlock(blockinfo[0], blockinfo[1]);
        }
    }

    private List<PlantInfo> plants = Lists.newArrayList();

    private PlantInfo currentPlant = null;

    public static final RightClickCropHandler INSTANCE = new RightClickCropHandler();

    private RightClickCropHandler() {}

    public void addCrop(PlantInfo info) {
        plants.add(info);
    }

    @SubscribeEvent
    public void handleCropRightClick(PlayerInteractEvent event) {
        int x = event.x, y = event.y, z = event.z;
        Block block = event.world.getBlock(x, y, z);
        int meta = event.world.getBlockMetadata(x, y, z);

        ItemStack stack = event.entityPlayer.getHeldItem();
        int range = getRange(stack);

        boolean handHarvest = ConfigHandler.allowCropRC && (stack == null && !event.entityPlayer.isSneaking());
        boolean sickleHarvest = ConfigHandler.allowSickleRC && range > 0;

        if ((handHarvest || sickleHarvest) && event.action == Action.RIGHT_CLICK_BLOCK
                && !(event.entityPlayer instanceof FakePlayer)) {
            for (PlantInfo info : plants) {
                if (info.blockInst == block && meta == info.meta) {
                    if (event.world.isRemote) {
                        event.entityPlayer.swingItem();
                    } else {
                        int fortune = 0;
                        int cropsHarvested = 0;

                        if (sickleHarvest) {
                            fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
                        }

                        for (int i = x - range; i <= x + range; i++) {
                            for (int k = z - range; k <= z + range; k++)
                                if (info.blockInst == event.world.getBlock(i, y, k)
                                        && event.world.getBlockMetadata(i, y, k) == info.meta) {
                                            currentPlant = info;
                                            block.dropBlockAsItem(event.world, i, y, k, meta, fortune);
                                            cropsHarvested++;
                                            currentPlant = null;
                                            event.world.setBlockMetadataWithNotify(i, y, k, info.resetMeta, 3);
                                            event.setCanceled(true);
                                        }
                        }
                        damageDurability(stack, cropsHarvested, event.entityPlayer);
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestDrop(HarvestDropsEvent event) {
        if (currentPlant != null) {
            for (int i = 0; i < event.drops.size(); i++) {
                ItemStack stack = event.drops.get(i);
                if (stack.getItem() == currentPlant.seedStack.getItem()
                        && (currentPlant.seedStack.getItemDamage() == OreDictionary.WILDCARD_VALUE
                                || stack.getItemDamage() == currentPlant.seedStack.getItemDamage())) {
                    event.drops.remove(i);
                    break;
                }
            }
        }
    }

    public int getRange(ItemStack tool) {
        if (tool != null) {
            Item item = tool.getItem();
            int defaultValue = 3;

            // Magia Naturalis
            if (item instanceof SickleItem) return defaultValue;

            // Redstone Arsenal
            if (item instanceof ItemSickleRF) {
                if (!ToolUtil.canDoEnergyOperations(tool)) return 1;
                else if (((ItemSickleRF) item).isEmpowered(tool)) return 5;
            }

            // Thermal Foundation
            if (item instanceof ItemSickleAdv) return ((ItemSickleAdv) item).radius;
        }
        return 0;
    }

    public void damageDurability(ItemStack tool, int cropsHarvested, EntityPlayer player) {
        if (ToolUtil.usesEnergy(tool, cropsHarvested)) return;

        if (tool.getItem() instanceof ItemTool) tool.damageItem(cropsHarvested, player);
    }
}
