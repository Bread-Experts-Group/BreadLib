package org.bread_experts_group.breadlib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlockEntity extends BlockEntity {
    public TestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityTypeTest.TEST_TYPE.get(), pPos, pBlockState);
    }
}
