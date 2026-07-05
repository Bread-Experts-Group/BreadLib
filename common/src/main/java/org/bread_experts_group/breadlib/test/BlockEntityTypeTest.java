package org.bread_experts_group.breadlib.test;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.registry.BlockEntityTypeBuilder;
import org.bread_experts_group.breadlib.registry.objects.RegistryObject;
import org.bread_experts_group.breadlib.registry.RegistryProvider;

public class BlockEntityTypeTest {
	public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY =
			new RegistryProvider<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreadLib.MOD_ID);

	public static final RegistryObject<BlockEntityType<?>, BlockEntityType<TestBlockEntity>> TEST_TYPE = BLOCK_ENTITY_TYPE_REGISTRY.register(
			"test_block_entity",
			() -> BlockEntityTypeBuilder
					.create(TestBlockEntity::new, BlocksTest.TEST_BLOCK.get())
//					.withRenderer(provider -> new TestBlockEntityRenderer())
					.build()
	);

	public static final RegistryObject<BlockEntityType<?>, BlockEntityType<QuarryBlockEntity>> QUARRY_TYPE = BLOCK_ENTITY_TYPE_REGISTRY.register(
			"quarry_block_entity",
			() -> BlockEntityTypeBuilder
					.create(QuarryBlockEntity::new, BlocksTest.QUARRY.get())
//					.withRenderer(provider -> new TestBlockEntityRenderer())
					.build()
	);
}