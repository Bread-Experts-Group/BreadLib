package org.bread_experts_group.breadlib.test;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.registry.objects.RegistryItem;

public class ItemsTest {
	public static final RegistryProvider.Items ITEM_REGISTRY = RegistryProvider.createItems(BreadLib.MOD_ID);

	public static final RegistryItem<BlockItem> TEST_BLOCK_ITEM = ITEM_REGISTRY.registerSimpleBlockItem("test_block", BlocksTest.TEST_BLOCK, new Item.Properties());
	public static final RegistryItem<TestItem> TEST_ITEM = ITEM_REGISTRY.register("test_item", TestItem::new);

	public static final RegistryItem<BlockItem> QUARRY_BLOCK_ITEM = ITEM_REGISTRY.registerSimpleBlockItem("quarry", BlocksTest.QUARRY, new Item.Properties());
}