package com.enderio.core.compat.nei;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.GuiContainerBase;

import codechicken.nei.guihook.IContainerObjectHandler;

public class EnderCoreContainerObjectHandler implements IContainerObjectHandler {

    @Override
    public void guiTick(GuiContainer guiContainer) {}

    @Override
    public void refresh(GuiContainer guiContainer) {}

    @Override
    public void load(GuiContainer guiContainer) {}

    @Override
    public ItemStack getStackUnderMouse(GuiContainer guiContainer, int mouseX, int mouseY) {
        if (guiContainer instanceof GuiContainerBase) {
            return ((GuiContainerBase) guiContainer).getHoveredStack(mouseX, mouseY);
        }
        return null;
    }

    @Override
    public boolean objectUnderMouse(GuiContainer guiContainer, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public boolean shouldShowTooltip(GuiContainer guiContainer) {
        return true;
    }
}
