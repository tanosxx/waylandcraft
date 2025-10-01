package dev.evvie.waylandcraft.bridge;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import dev.evvie.waylandcraft.BufferTexture;

public class WLCBuffer {
	
	public final int width;
	public final int height;
	
	// SHM data pointer. Can be zero for non-shm buffers!
	public final long shmDataPtr;
	
	protected WLCBuffer(int width, int height, long shmDataPtr) {
		this.width = width;
		this.height = height;
		this.shmDataPtr = shmDataPtr;
	}
	
	public BufferTexture getAsTexture() {
		ByteBuffer buf = MemoryUtil.memByteBuffer(shmDataPtr, width * height * 4);
		BufferTexture tex = new BufferTexture(buf, width, height);
		return tex;
	}
	
}
