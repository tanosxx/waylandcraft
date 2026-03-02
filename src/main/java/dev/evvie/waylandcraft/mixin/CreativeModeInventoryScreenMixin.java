package dev.evvie.waylandcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.evvie.waylandcraft.WaylandCraft;
import dev.evvie.waylandcraft.item.InventoryWindowContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
	
	public CreativeModeInventoryScreenMixin(ItemPickerMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
		throw new IllegalStateException();
	}
	
	private static final ResourceLocation INVENTORY_BAR = new ResourceLocation(WaylandCraft.MOD_ID, "textures/gui/inventory_bar.png");
	
	@Inject(method = "renderBg", at = @At("TAIL"))
	public void injectRenderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY, CallbackInfo info) {
		guiGraphics.blit(INVENTORY_BAR, leftPos + 19, topPos - 30, 0, 0, 256, 256);
	}
	
	@Inject(method = "selectTab", at = @At("TAIL"))
	public void injectSelectTab(CreativeModeTab creativeModeTab, CallbackInfo info) {
		for(Slot slot : menu.slots) {
			if(slot.container instanceof InventoryWindowContainer) {
				int idx = ((ICreativeModeInventoryScreenSlotWrapperMixin) slot).getTarget().getContainerSlot();
				((ISlotMixin) slot).setX(206);
				((ISlotMixin) slot).setY(18 + 18 * idx);
				
				System.out.println("FOUND SLOT " + idx);
			}
		}
	}
	
}
