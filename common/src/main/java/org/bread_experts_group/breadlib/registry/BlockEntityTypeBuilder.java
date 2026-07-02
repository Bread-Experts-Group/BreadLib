package org.bread_experts_group.breadlib.registry;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeBuilder<T extends BlockEntity> {
	private final BlockEntityType<T> type;

	private BlockEntityTypeBuilder(BlockEntityType<T> type) {
		this.type = type;
	}

	public static <T extends BlockEntity> BlockEntityTypeBuilder<T> create(BlockEntityType.BlockEntitySupplier<T> factory, Block... validBlocks) {
		BlockEntityType<T> type = BlockEntityType.Builder.of(factory, validBlocks).build(null);
		return new BlockEntityTypeBuilder<>(type);
	}

	public BlockEntityTypeBuilder<T> withRenderer(BlockEntityRendererProvider<T> provider) {
		BlockEntityRenderers.register(this.type, provider);
		return this;
	}

	public BlockEntityType<T> build() {
		return type;
	}
}