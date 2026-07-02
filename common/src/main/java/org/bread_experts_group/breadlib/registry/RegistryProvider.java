package org.bread_experts_group.breadlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock;
import org.bread_experts_group.breadlib.registry.objects.RegistryItem;
import org.bread_experts_group.breadlib.registry.objects.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class RegistryProvider<T> {
	private final Map<RegistryObject<T, ? extends T>, Supplier<T>> entries = new HashMap<>();
	private final Collection<RegistryObject<T, ? extends T>> entriesViewOnly = Collections.unmodifiableSet(entries.keySet());
	private final Registry<T> registry;
	private final ResourceKey<Registry<T>> key;
	private final String modID;
	private boolean frozen = false;

	public static ArrayList<RegistryProvider<?>> providers = new ArrayList<>();

	public static void registerAll(RegistryProvider<?>... providers) {
		for (RegistryProvider<?> provider : providers) provider.register();
	}

	public static RegistryProvider.Blocks createBlocks(String modID) {
		return new Blocks(modID);
	}

	public static RegistryProvider.Items createItems(String modID) {
		return new Items(modID);
	}

	public RegistryProvider(Registry<T> registry, String modID) {
		this.registry = registry;
		this.key = ResourceKey.createRegistryKey(registry.key().location());
		this.modID = modID;
	}

	public void register() {
		providers.add(this);
		this.frozen = true;
	}

	public Collection<RegistryObject<T, ? extends T>> entriesView() {
		return entriesViewOnly;
	}

	public Map<RegistryObject<T, ? extends T>, Supplier<T>> entries() {
		return this.entries;
	}

	public ResourceKey<Registry<T>> getKey() {
		return this.key;
	}

	public Registry<T> getRegistry() {
		return this.registry;
	}

	public String getModID() {
		return modID;
	}

	public <I extends T> RegistryObject<T, I> createRegistryObject(String name) {
		return RegistryObject.create(this.modID, name, this.registry);
	}

	public <I extends T> RegistryObject<T, I> register(String name, Supplier<T> supplier) {
		if (this.frozen) throw new IllegalStateException("Provider is already frozen.");
		RegistryObject<T, I> regObject = this.createRegistryObject(name);
		if (this.entries.putIfAbsent(regObject, supplier) != null) {
			throw new IllegalStateException("Duplicate registry entry: " + this.modID + ":" + name);
		}
		return regObject;
	}

	public static class Blocks extends RegistryProvider<Block> {
		private Blocks(String modID) {
			super(BuiltInRegistries.BLOCK, modID);
		}

		@Override
		public <B extends Block> RegistryBlock<B> createRegistryObject(String name) {
			return RegistryBlock.create(this.getModID(), name);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <B extends Block> RegistryBlock<B> register(String name, Supplier<Block> supplier) {
			return (RegistryBlock<B>) super.register(name, supplier);
		}

		public <B extends Block> RegistryBlock<B> registerSimpleBlock(String name, BlockBehaviour.Properties properties) {
			return this.register(name, () -> new Block(properties));
		}
	}

	public static class Items extends RegistryProvider<Item> {
		private Items(String modID) {
			super(BuiltInRegistries.ITEM, modID);
		}

		@Override
		public <I extends Item> RegistryItem<I> createRegistryObject(String name) {
			return RegistryItem.create(this.getModID(), name);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <I extends Item> RegistryItem<I> register(String name, Supplier<Item> supplier) {
			return (RegistryItem<I>) super.register(name, supplier);
		}

		public <I extends Item> RegistryItem<I> simpleItem(String name, Item.Properties properties) {
			return this.register(name, () -> new Item(properties));
		}

		public <B extends BlockItem> RegistryItem<B> registerSimpleBlockItem(String name, Supplier<Block> block, Item.Properties properties) {
			return this.register(name, () -> new BlockItem(block.get(), properties));
		}
	}
}
