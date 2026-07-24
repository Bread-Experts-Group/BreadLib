package org.bread_experts_group.breadlib.test

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.createItems
import org.bread_experts_group.breadlib.registry.objects.RegistryItem

object ItemsTest {
	val ITEM_REGISTRY: RegistryProvider.Items = createItems(BreadLib.MOD_ID)

	val TEST_BLOCK_ITEM: RegistryItem<BlockItem> =
		ITEM_REGISTRY.registerSimpleBlockItem("test_block", BlocksTest.TEST_BLOCK, Item.Properties())

	val TEST_ITEM: RegistryItem<TestItem> = ITEM_REGISTRY.register("test_item", ::TestItem)

	val QUARRY_BLOCK_ITEM: RegistryItem<BlockItem> =
		ITEM_REGISTRY.registerSimpleBlockItem("quarry", BlocksTest.QUARRY, Item.Properties())
}