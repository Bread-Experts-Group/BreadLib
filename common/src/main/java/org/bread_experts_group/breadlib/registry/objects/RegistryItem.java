package org.bread_experts_group.breadlib.registry.objects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.registry.ItemLikeExtended;
import org.jetbrains.annotations.NotNull;

public class RegistryItem<I extends Item> extends RegistryObject<Item, I> implements ItemLikeExtended {
	public static <I extends Item> RegistryItem<I> create(String modID, String name) {
		return new RegistryItem<>(ResourceLocation.fromNamespaceAndPath(modID, name));
	}

	public RegistryItem(ResourceLocation name) {
		super(name, BuiltInRegistries.ITEM);
	}

	@Override
	public @NotNull Item asItem() {
		return get();
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
