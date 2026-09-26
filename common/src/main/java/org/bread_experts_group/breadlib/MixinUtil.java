package org.bread_experts_group.breadlib;

import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.registry.client.IClientItemExtension;

import java.util.HashMap;
import java.util.Map;

public class MixinUtil {
	public static Map<Item, IClientItemExtension> CLIENT_ITEM_EXTENSIONS = new HashMap<>();
}