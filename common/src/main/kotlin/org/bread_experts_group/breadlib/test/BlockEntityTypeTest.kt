package org.bread_experts_group.breadlib.test

import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.BlockEntityTypeRegistryObject
import org.bread_experts_group.breadlib.registry.RegistryProvider

object BlockEntityTypeTest {
	val BLOCK_ENTITY_TYPE_REGISTRY: RegistryProvider.BlockEntityTypes = RegistryProvider.getBlockEntityTypes(BreadLib.MOD_ID)

	val TEST_TYPE: BlockEntityTypeRegistryObject<TestBlockEntity> = this.BLOCK_ENTITY_TYPE_REGISTRY.register("test_block_entity") {
		create(::TestBlockEntity)
			.withRenderer(::TestBlockEntityRenderer)
	}

//	val QUARRY_TYPE: BlockEntityTypeRegistryObject<QuarryBlockEntity> = this.BLOCK_ENTITY_TYPE_REGISTRY.register("quarry_block_entity") {
//		create(::QuarryBlockEntity)
//	}
}