package dev.evvie.waylandcraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.inventory.Slot;

@Mixin(Slot.class)
public interface ISlotMixin {
	
	@Accessor
	@Mutable
	public void setX(int x);
	
	@Accessor
	@Mutable
	public void setY(int y);
	
}
