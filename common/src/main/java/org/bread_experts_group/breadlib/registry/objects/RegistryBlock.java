package org.bread_experts_group.breadlib.registry.objects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadlib.registry.ItemLikeExtended;
import org.jetbrains.annotations.NotNull;

public class RegistryBlock<B extends Block> extends RegistryObject<Block, B> implements ItemLikeExtended {
    public static <B extends Block> RegistryBlock<B> create(String modID, String name) {
        return new RegistryBlock<>(ResourceLocation.fromNamespaceAndPath(modID, name));
    }

    public RegistryBlock(ResourceLocation name) {
        super(name, BuiltInRegistries.BLOCK);
    }

    public BlockState defaultState() {
        return get().defaultBlockState();
    }

    @Override
    public @NotNull Item asItem() {
        return toStack().getItem();
    }

    @Override
    public ItemStack toStack() {
        return new ItemStack(this, 1);
    }

    @Override
    public ItemStack asStack(int count) {
        return new ItemStack(this, count);
    }
}
