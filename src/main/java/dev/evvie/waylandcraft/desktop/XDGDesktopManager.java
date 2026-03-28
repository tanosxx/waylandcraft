package dev.evvie.waylandcraft.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;

import dev.evvie.waylandcraft.WaylandCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class XDGDesktopManager {
	
	private final WaylandCraft wlc;
	private ArrayList<DesktopEntry> systemEntries = null;
	private Thread systemEntryFetchThread;
	private boolean iconsUploaded = false;
	
	public XDGDesktopManager(WaylandCraft wlc) {
		this.wlc = wlc;
		systemEntryFetchThread = new Thread(this::loadSystemEntries);
		systemEntryFetchThread.start();
	}
	
	private void loadSystemEntries() {
		/* Calling this in a separate thread is probably a huge crime but I'm desperate */
		
		Instant start = Instant.now();
		
		RawDesktopEntry[] rawEntries = wlc.bridge.loadSystemDesktopEntries();
		ArrayList<DesktopEntry> systemEntries = new ArrayList<DesktopEntry>();
		for(RawDesktopEntry raw : rawEntries) {
			systemEntries.add(new DesktopEntry(raw.appId, raw.name, raw.genericName, raw.exec, raw.execTerminal, raw.visible, raw.iconPath));
		}
		this.systemEntries = systemEntries;
		
		WaylandCraft.LOGGER.info("Completed desktop entry loading in " + Duration.between(start, Instant.now()).toMillis() / 1000.0f + "s");
	}
	
	private boolean completeFetch() {
		boolean done = false;
		try {
			done = systemEntryFetchThread.join(Duration.ZERO);
		} catch(InterruptedException e) {
		}
		
		if(done) {
			if(!iconsUploaded) {
				uploadIcons();
				iconsUploaded =  true;
			}
			return true;
		}
		
		return false;
	}
	
	public List<DesktopEntry> entries() {
		if(!completeFetch()) {
			return new ArrayList<DesktopEntry>();
		}
		
		ArrayList<DesktopEntry> entries = new ArrayList<DesktopEntry>();
		entries.addAll(systemEntries);
		return entries;
	}
	
	public @Nullable DesktopEntry forAppId(String appId) {
		if(!completeFetch()) {
			return null;
		}
		
		for(DesktopEntry entry : systemEntries) {
			if(entry.appId.equals(appId)) return entry;
		}
		return null;
	}
	
	private void uploadIcons() {
		Instant start = Instant.now();
		
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		
		for(DesktopEntry entry : systemEntries) {
			IconTexture texture = tryLoadIcon(entry.iconPath);
			if(texture != null) {
				ResourceLocation location = new ResourceLocation(WaylandCraft.MOD_ID, "icon_" + DigestUtils.sha1Hex(entry.appId));
				textureManager.register(location, texture);
				entry.icon = location;
			}
		}
		
		WaylandCraft.LOGGER.info("Completed icon uploading in " + Duration.between(start, Instant.now()).toMillis() / 1000.0f + "s");
	}
	
	public @Nullable String getName(String appId) {
		DesktopEntry entry = forAppId(appId);
		if(entry == null) return null;
		return entry.name;
	}
	
	public @Nullable ResourceLocation getIcon(String appId) {
		DesktopEntry entry = forAppId(appId);
		if(entry == null) return null;
		return entry.icon;
	}
	
	private String getExtension(File file) {
		String path = file.getAbsolutePath();
		int idx = path.lastIndexOf('.');
		if(idx < 0 || idx >= path.length() - 1) return "";
		
		return path.substring(idx + 1);
	}
	
	private IconTexture tryLoadIcon(String iconPath) {
		try {
			return loadIcon(iconPath);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private IconTexture loadIcon(String iconPath) throws IOException {
		if(iconPath == null) return null;
		
		File iconFile = new File(iconPath);
		
		/* This "file type check" is valid because according to the Icon Theme Specification
		 * the extension has to be one of ".png", ".xpm" and ".svg" (lowercase) and the extension
		 * signals what type of file we should expect.
		 */
		if(!getExtension(iconFile).equals("png")) {
			return null;
		}
		
		return new IconTexture(iconFile);
	}
	
	public static class IconTexture extends AbstractTexture {
		
		private final NativeImage image;
		
		public IconTexture(File file) throws IOException {
			FileInputStream stream = new FileInputStream(file);
			image = NativeImage.read(stream);
			TextureUtil.prepareImage(getId(), image.getWidth(), image.getHeight());
			image.upload(0, 0, 0, false);
		}
		
		@Override
		public void load(ResourceManager resourceManager) throws IOException {
		}
		
		@Override
		public void close() {
			if(image != null) {
				image.close();
				releaseId();
			}
		}
		
	}
	
}
