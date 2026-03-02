package dev.evvie.waylandcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.evvie.waylandcraft.WaylandCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
	
	public InventoryScreenMixin(InventoryMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
		throw new IllegalStateException();
	}
	
	private static final ResourceLocation INVENTORY_BAR = new ResourceLocation(WaylandCraft.MOD_ID, "textures/gui/inventory_bar.png");
	
	@Inject(method = "renderBg", at = @At("TAIL"))
	public void injectRenderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY, CallbackInfo info) {
		guiGraphics.blit(INVENTORY_BAR, leftPos, topPos, 0, 0, 256, 256);
	}
	
}
