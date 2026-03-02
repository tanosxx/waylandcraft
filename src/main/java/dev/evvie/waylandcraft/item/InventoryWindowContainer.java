package dev.evvie.waylandcraft.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryWindowContainer implements Container {
	
	@Override
	public void clearContent() {
	}
	
	@Override
	public int getContainerSize() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public ItemStack getItem(int i) {
		return new ItemStack(WindowItem.WINDOW);
	}
	
	@Override
	public ItemStack removeItem(int i, int j) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int i) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItem(int i, ItemStack itemStack) {
	}
	
	@Override
	public void setChanged() {
	}
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
}
