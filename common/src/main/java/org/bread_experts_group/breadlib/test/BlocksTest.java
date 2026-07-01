package org.bread_experts_group.breadlib.test;

import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock;
import org.bread_experts_group.breadlib.registry.RegistryProvider;

public class BlocksTest {
    public static final RegistryProvider.Blocks BLOCK_REGISTRY = RegistryProvider.createBlocks(BreadLib.MOD_ID);

    public static RegistryBlock<TestBlock> TEST_BLOCK = BLOCK_REGISTRY.register("test_block", TestBlock::new);
}