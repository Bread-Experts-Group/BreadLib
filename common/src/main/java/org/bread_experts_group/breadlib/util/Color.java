package org.bread_experts_group.breadlib.util;

public class Color {
	public static final int NONE = color(0, 0, 0, 0);
	public static final int WHITE = color(255, 255, 255, 255);
	public static final int BLACK = color(0 ,0 ,0, 255);
	public static final int GRAY = color(128, 128, 128, 255);
	public static final int LIGHT_GRAY = color(192, 192, 192, 255);

	public static int color(int r, int g, int b, int a) {
		return (((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}
}