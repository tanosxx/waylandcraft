package dev.evvie.waylandcraft.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.evvie.waylandcraft.CursorShape;
import dev.evvie.waylandcraft.WaylandCraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public class GuiMixin {
	
	private static final ResourceLocation TLBR_DIAGONAL_CROSSHAIR = new ResourceLocation(WaylandCraft.MOD_ID, "crosshair/tlbr_diagonal");
	private static final ResourceLocation TRBL_DIAGONAL_CROSSHAIR = new ResourceLocation(WaylandCraft.MOD_ID, "crosshair/trbl_diagonal");
	private static final ResourceLocation LEFT_RIGHT_CROSSHAIR = new ResourceLocation(WaylandCraft.MOD_ID, "crosshair/left_right");
	private static final ResourceLocation TOP_BOTTOM_CROSSHAIR = new ResourceLocation(WaylandCraft.MOD_ID, "crosshair/top_bottom");
	
	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
	public void crosshairBlitSprite(GuiGraphics context, ResourceLocation original, int x, int y, int width, int height) {
		CursorShape cursor = WaylandCraft.instance.cursorShape;
		ResourceLocation crosshair = crosshairForCursor(cursor);
		if(crosshair == null) crosshair = original;
		
		context.blitSprite(crosshair, x, y, width, height);
	}
	
	private @Nullable ResourceLocation crosshairForCursor(@Nullable CursorShape cursor) {
		if(cursor == null) return null;
		
		switch(cursor) {
		case DEFAULT: return null;
		case HIDE: return null;
		case E_RESIZE: return LEFT_RIGHT_CROSSHAIR;
		case N_RESIZE: return TOP_BOTTOM_CROSSHAIR;
		case NE_RESIZE: return TRBL_DIAGONAL_CROSSHAIR;
		case NW_RESIZE: return TLBR_DIAGONAL_CROSSHAIR;
		case S_RESIZE: return TOP_BOTTOM_CROSSHAIR;
		case SE_RESIZE: return TLBR_DIAGONAL_CROSSHAIR;
		case SW_RESIZE: return TRBL_DIAGONAL_CROSSHAIR;
		case W_RESIZE: return LEFT_RIGHT_CROSSHAIR;
		case EW_RESIZE: return LEFT_RIGHT_CROSSHAIR;
		case NS_RESIZE: return TOP_BOTTOM_CROSSHAIR;
		case NESW_RESIZE: return TRBL_DIAGONAL_CROSSHAIR;
		case NWSE_RESIZE: return TLBR_DIAGONAL_CROSSHAIR;
		case COL_RESIZE: return LEFT_RIGHT_CROSSHAIR;
		case ROW_RESIZE: return TOP_BOTTOM_CROSSHAIR;
		default: return null;
		}
	}
	
}
