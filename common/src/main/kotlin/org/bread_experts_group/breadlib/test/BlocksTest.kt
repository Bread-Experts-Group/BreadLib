package org.bread_experts_group.breadlib.test

import net.minecraft.world.item.BlockItem
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlocks
import org.bread_experts_group.breadlib.registry.objects.RegistryBlockItem

object BlocksTest {
	val REGISTRY: RegistryProvider.Blocks = getBlocks(BreadLib.MOD_ID)

	val TEST_BLOCK: RegistryBlockItem<TestBlock, BlockItem> = REGISTRY.register<TestBlock>("test_block", false, ::TestBlock)
		.withItem()
	val QUARRY: RegistryBlockItem<QuarryBlock, BlockItem> = REGISTRY.register<QuarryBlock>("quarry", false, ::QuarryBlock)
		.withItem()
	val MP_CABLE: RegistryBlockItem<MultipartCableBlock, BlockItem> = REGISTRY.register<MultipartCableBlock>("multipart_cable", false, ::MultipartCableBlock)
		.withItem()
}