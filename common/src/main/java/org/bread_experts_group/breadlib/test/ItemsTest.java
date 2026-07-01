package org.bread_experts_group.breadlib.test;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.registry.objects.RegistryItem;

public class ItemsTest {
	public static RegistryProvider.Items ITEM_REGISTRY = RegistryProvider.createItems(BreadLib.MOD_ID);

	public static RegistryItem<BlockItem> TEST_ITEM = ITEM_REGISTRY.registerSimpleBlockItem("test_item", BlocksTest.TEST_BLOCK, new Item.Properties());
}
