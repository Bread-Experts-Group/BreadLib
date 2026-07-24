package org.bread_experts_group.breadlib.registry

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import java.util.*
import java.util.function.Supplier

open class RegistryProvider<T>(val registry: Registry<T>, val modID: String) {
	companion object {
		@JvmField
		val providers: ArrayList<RegistryProvider<*>> = arrayListOf()

		@JvmStatic
		fun registerAll(vararg providers: RegistryProvider<*>) {
			for (provider in providers) provider.register()
		}

		@JvmStatic
		fun createBlocks(modID: String): Blocks = Blocks(modID)

		@JvmStatic
		fun createItems(modID: String): Items = Items(modID)
	}

	private val entries: MutableMap<RegistryObject<T, out T>, Supplier<T>> = hashMapOf()
	private val entriesViewOnly: MutableCollection<RegistryObject<T, out T>> =
		Collections.unmodifiableSet<RegistryObject<T, out T>>(this.entries.keys)
	val key: ResourceKey<Registry<T>> = ResourceKey.createRegistryKey(this.registry.key().location())

	private var frozen: Boolean = false

	fun register() {
		providers.add(this)
		this.frozen = true
	}

	fun entriesView(): Collection<RegistryObject<T, out T>> = this.entriesViewOnly

	fun entries(): MutableMap<RegistryObject<T, out T>, Supplier<T>> = this.entries

	open fun <I : T> createRegistryObject(name: String): RegistryObject<T, I> =
		RegistryObject.create(this.modID, name, this.registry)

	open fun <I : T> register(name: String, supplier: Supplier<T>): RegistryObject<T, I> {
		check(!this.frozen) { "Provider is already frozen." }
		val regObject = this.createRegistryObject<I>(name)
		check(this.entries.putIfAbsent(regObject, supplier) == null) {
			"Duplicate registry entry: " + this.modID + ":" + name
		}
		return regObject
	}

	class Blocks(modID: String) : RegistryProvider<Block>(BuiltInRegistries.BLOCK, modID) {

		override fun <B : Block> createRegistryObject(name: String): RegistryBlock<B> =
			RegistryBlock.create<B>(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <B : Block> register(name: String, supplier: Supplier<Block>): RegistryBlock<B> =
			super.register<Block>(name, supplier) as RegistryBlock<B>

		fun <B : Block> registerSimpleBlock(name: String, properties: BlockBehaviour.Properties): RegistryBlock<B> =
			this.register(name) { Block(properties) }
	}

	class Items(modID: String) : RegistryProvider<Item>(BuiltInRegistries.ITEM, modID) {

		override fun <I : Item> createRegistryObject(name: String): RegistryItem<I> =
			RegistryItem.create(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <I : Item> register(name: String, supplier: Supplier<Item>): RegistryItem<I> =
			super.register<Item>(name, supplier) as RegistryItem<I>

		fun <I : Item> simpleItem(name: String, properties: Item.Properties): RegistryItem<I> =
			this.register(name) { Item(properties) }

		fun <B : BlockItem> registerSimpleBlockItem(
			name: String,
			block: Supplier<Block>,
			properties: Item.Properties
		): RegistryItem<B> = this.register(name) { BlockItem(block.get(), properties) }
	}
}
