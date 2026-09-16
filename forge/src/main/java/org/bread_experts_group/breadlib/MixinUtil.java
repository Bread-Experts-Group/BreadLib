package org.bread_experts_group.breadlib;

import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.registry.client.IClientItemExtension;

import java.util.HashMap;
import java.util.Map;

public class MixinUtil {
	/**
	 * Forge doesn't have its own item extensions event like neo, so this just exists to reference in a mixin in IClientItemExtensions.
	 */
	public static Map<Item, IClientItemExtension> itemExtensions = new HashMap<>();
}
