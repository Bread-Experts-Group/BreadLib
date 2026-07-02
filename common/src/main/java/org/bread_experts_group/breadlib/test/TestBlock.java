package org.bread_experts_group.breadlib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends Block implements EntityBlock {
	public TestBlock() {
		super(BlockBehaviour.Properties.of());
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TestBlockEntity(pos, state);
	}
}