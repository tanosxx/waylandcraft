package dev.evvie.waylandcraft.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import dev.evvie.waylandcraft.WaylandCraft;
import net.minecraft.client.Minecraft;

public class WaylandCraftSettingsManager {
	
	private final WaylandCraft wlc;
	private final Gson gson = new Gson();
	
	private final File settingsDir;
	private final File settingsFile;
	private final File keymapFile;
	
	public String keymap = null;
	private boolean createKeymap = false;
	
	public WaylandCraftSettingsManager(WaylandCraft wlc) throws IOException {
		this.wlc = wlc;
		
		settingsDir = new File(Minecraft.getInstance().gameDirectory, "waylandcraft");
		if(!settingsDir.exists()) {
			settingsDir.mkdir();
		}
		else if(!settingsDir.isDirectory()) {
			throw new IOException("Waylandcraft settings directory exists but is not a directory");
		}
		
		boolean createSettings = false;
		settingsFile = new File(settingsDir, "settings.json");
		if(!settingsFile.exists()) {
			settingsFile.createNewFile();
			createSettings = true;
		}
		else if(!settingsFile.isFile()) {
			throw new IOException("Waylandcraft settings.json exists but is not a file");
		}
		
		keymapFile = new File(settingsDir, "keymap.txt");
		if(!keymapFile.exists()) {
			keymapFile.createNewFile();
			createKeymap = true;
		}
		else if(!keymapFile.isFile()) {
			throw new IOException("Waylandcraft keymap.txt exists but is not a file");
		}
		
		if(createSettings) {
			wlc.settings = new WaylandCraftSettings();
			writeSettings();
		}
		
		readSettings();
	}
	
	public void readSettings() {
		try {
			FileReader reader = new FileReader(settingsFile);
			wlc.settings = gson.fromJson(reader, WaylandCraftSettings.class);
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeSettings() {
		try {
			String json = gson.toJson(wlc.settings, WaylandCraftSettings.class);
			FileWriter writer = new FileWriter(settingsFile);
			writer.write(json);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
