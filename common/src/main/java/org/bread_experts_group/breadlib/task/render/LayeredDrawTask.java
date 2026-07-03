package org.bread_experts_group.breadlib.task.render;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.bread_experts_group.breadlib.task.Task;

import java.util.HashMap;
import java.util.Map;

// todo order based layer adding for all three platforms (render our layers behind or in front of other layers)
public class LayeredDrawTask extends Task {
	private final Map<ResourceLocation, LayeredDraw.Layer> layers = new HashMap<>();

	public Map<ResourceLocation, LayeredDraw.Layer> getLayers() {
		return layers;
	}

	public void add(ResourceLocation location, LayeredDraw.Layer layer) {
		this.layers.put(location, layer);
	}
}