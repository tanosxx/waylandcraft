package dev.evvie.waylandcraft;

import org.jetbrains.annotations.Nullable;

public enum CursorShape {
	
	HIDE(0),
	DEFAULT(1),
	E_RESIZE(18),
	N_RESIZE(19),
	NE_RESIZE(20),
	NW_RESIZE(21),
	S_RESIZE(22),
	SE_RESIZE(23),
	SW_RESIZE(24),
	W_RESIZE(25),
	EW_RESIZE(26),
	NS_RESIZE(27),
	NESW_RESIZE(28),
	NWSE_RESIZE(29),
	COL_RESIZE(30),
	ROW_RESIZE(31);
	
	// Serialization number for cursor shape. Should match cursor-shape wayland protocol for all cursors except extensions.
	public final int id;
	
	private CursorShape(int id) {
		this.id = id;
	}
	
	public static @Nullable CursorShape fromId(int id) {
		for(CursorShape shape : values()) {
			if(shape.id == id) return shape;
		}
		return null;
	}
	
}
