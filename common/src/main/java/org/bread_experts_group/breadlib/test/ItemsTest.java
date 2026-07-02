package org.bread_experts_group.breadlib.test;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.registry.objects.RegistryItem;

public class ItemsTest {
	public static RegistryProvider.Items ITEM_REGISTRY = RegistryProvider.createItems(BreadLib.MOD_ID);

	public static RegistryItem<BlockItem> TEST_ITEM_BLOCK =
			ITEM_REGISTRY.registerSimpleBlockItem("test_item_block", BlocksTest.TEST_BLOCK, new Item.Properties());
	public static RegistryItem<TestItem> TEST_ITEM = ITEM_REGISTRY.register("test_item", TestItem::new);
}
