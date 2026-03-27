package dev.evvie.waylandcraft.gui;

import dev.evvie.waylandcraft.WaylandCraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WaylandCraftSettingsScreen extends Screen {
	
	private final WaylandCraft wlc;
	private Screen lastScreen;
	
	protected WaylandCraftSettingsScreen(WaylandCraft wlc, Screen lastScreen) {
		super(Component.literal("Waylandcraft Settings"));
		this.wlc = wlc;
		this.lastScreen = lastScreen;
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(lastScreen);
	}
	
}
