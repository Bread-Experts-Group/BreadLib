package org.bread_experts_group.breadlib.test

import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.createBlocks
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock

object BlocksTest {
	val BLOCK_REGISTRY: RegistryProvider.Blocks = createBlocks(BreadLib.MOD_ID)

	val TEST_BLOCK: RegistryBlock<TestBlock> = BLOCK_REGISTRY.register("test_block", ::TestBlock)
	val QUARRY: RegistryBlock<QuarryBlock> = BLOCK_REGISTRY.register("quarry", ::QuarryBlock)
}