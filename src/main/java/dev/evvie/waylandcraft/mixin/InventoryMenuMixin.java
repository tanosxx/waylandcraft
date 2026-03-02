package dev.evvie.waylandcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.evvie.waylandcraft.item.InventoryWindowContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingContainer> {
	
	public InventoryMenuMixin(MenuType<?> menuType, int containerId) {
		super(menuType, containerId);
		throw new IllegalStateException();
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(Inventory inventory, boolean serverSide, Player player, CallbackInfo info) {
		for(int i = 0; i < 4; i++) {
			addSlot(new Slot(new InventoryWindowContainer(), i, 187, 48 + 18 * i));
		}
	}
	
}
