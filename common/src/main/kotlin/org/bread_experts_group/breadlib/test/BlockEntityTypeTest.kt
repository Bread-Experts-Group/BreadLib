package org.bread_experts_group.breadlib.test

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.BlockEntityTypeBuilder.Companion.create
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryObject

object BlockEntityTypeTest {
	val BLOCK_ENTITY_TYPE_REGISTRY: RegistryProvider<BlockEntityType<*>> =
		RegistryProvider<BlockEntityType<*>>(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreadLib.MOD_ID)

	val TEST_TYPE: RegistryObject<BlockEntityType<*>, BlockEntityType<TestBlockEntity>> =
		this.BLOCK_ENTITY_TYPE_REGISTRY.register("test_block_entity") {
			create(::TestBlockEntity, BlocksTest.TEST_BLOCK.get())
				.withRenderer(::TestBlockEntityRenderer)
				.build()
		}

	val QUARRY_TYPE: RegistryObject<BlockEntityType<*>, BlockEntityType<QuarryBlockEntity>> =
		this.BLOCK_ENTITY_TYPE_REGISTRY.register("quarry_block_entity") {
			create(::QuarryBlockEntity, BlocksTest.QUARRY.get()).build()
		}
}