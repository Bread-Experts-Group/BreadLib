package org.bread_experts_group.breadlib.registry

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.jetbrains.annotations.ApiStatus
import java.util.function.Supplier

typealias BlockEntityTypeRegistryObject<T> = RegistryObject<BlockEntityType<*>, BlockEntityType<T>>

open class RegistryProvider<T> private constructor(val registry: Registry<T>, val modID: String) {
	companion object {
		val providers: MutableMap<String, MutableMap<Registry<*>, RegistryProvider<*>>> = mutableMapOf()

		fun registerAll(vararg providers: RegistryProvider<*>) {
			for (provider in providers) provider.register()
		}

		fun getBlocks(modID: String): Blocks = BuiltInRegistries.BLOCK.get(modID) as Blocks
		fun getBlockEntityTypes(modID: String): BlockEntityTypes = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(modID) as BlockEntityTypes
		fun getItems(modID: String): Items = BuiltInRegistries.ITEM.get(modID) as Items

		@Suppress("UNCHECKED_CAST")
		fun <T> Registry<T>.get(modID: String): RegistryProvider<T> = providers.getOrPut(modID) { mutableMapOf() }.getOrPut(this) {
			when (this) {
				BuiltInRegistries.BLOCK -> Blocks(modID)
				BuiltInRegistries.ITEM -> Items(modID)
				BuiltInRegistries.BLOCK_ENTITY_TYPE -> BlockEntityTypes(modID)
				else -> RegistryProvider(this, modID)
			}
		} as RegistryProvider<T>
	}

	val entries: MutableMap<RegistryObject<T, out T>, Supplier<T>> = mutableMapOf()
	val key: ResourceKey<Registry<T>> = ResourceKey.createRegistryKey(this.registry.key().location())
	var frozen: String? = null
		private set

	fun register(source: String? = null) {
		this.frozen = source ?: "Mod registration"
	}

	open fun <I : T> createRegistryObject(name: String): RegistryObject<T, I> =
		RegistryObject.create(this.modID, name, this.registry)

	open fun <I : T> register(name: String, supplier: Supplier<T>): RegistryObject<T, I> {
//		this.frozen?.let {
//			throw IllegalStateException("Provider was already frozen: $it")
//		}
		val regObject = this.createRegistryObject<I>(name)
		check(this.entries.putIfAbsent(regObject, supplier) == null) {
			"Duplicate registry entry: " + this.modID + ":" + name
		}
		return regObject
	}

	class Blocks(modID: String) : RegistryProvider<Block>(BuiltInRegistries.BLOCK, modID) {
		override fun <B : Block> createRegistryObject(name: String): RegistryBlock<B> = RegistryBlock.create(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <B : Block> register(name: String, supplier: Supplier<Block>): RegistryBlock<B> = super.register<Block>(name, supplier) as RegistryBlock<B>
		fun <B : Block> registerSimpleBlock(name: String, properties: BlockBehaviour.Properties): RegistryBlock<B> = this.register(name) { Block(properties) }
	}

	class BlockEntityTypes(modID: String) : RegistryProvider<BlockEntityType<*>>(BuiltInRegistries.BLOCK_ENTITY_TYPE, modID) {
		inner class BlockEntityTypeBuilder<T : BlockEntity>(
			factory: BlockEntitySupplier<T>,
			validBlocks: Set<Block>
		) : BlockEntityType<T>(factory, validBlocks, null) {
			fun withRenderer(provider: BlockEntityRendererProvider<T>): BlockEntityTypeBuilder<T> = this.also {
				BlockEntityRenderers.register(this, provider)
			}
		}

		private val types = mutableMapOf<Class<*>, BlockEntityType<*>>()
		private val applicableBlocks: List<BreadLibBlockWithEntity<*>> by lazy {
			getBlocks(modID)
				.also { it.register() }
				.entries.keys
				.mapNotNull { it.get() as? BreadLibBlockWithEntity<*> }
		}

		inline fun <reified T : BlockEntity> create(
			noinline factory: (pos: BlockPos, state: BlockState) -> T
		): BlockEntityTypeBuilder<T> = create(T::class.java, factory)

		@ApiStatus.Internal
		fun <T : BlockEntity> create(
			returnClass: Class<T>,
			factory: (pos: BlockPos, state: BlockState) -> T
		): BlockEntityTypeBuilder<T> {
			val validBlocks = mutableSetOf<Block>()
			applicableBlocks.filterTo(validBlocks) { returnClass == it.blockEntity }
			val builder = BlockEntityTypeBuilder(factory, validBlocks)
			types[returnClass] = builder
			return builder
		}

		fun <I : BlockEntityType<*>> register(
			name: String,
			supplier: BlockEntityTypes.() -> BlockEntityType<*>
		): RegistryObject<BlockEntityType<*>, I> {
			return super.register(name) { supplier() }
		}

		internal fun getType(clazz: Class<*>): BlockEntityType<*>? = this.types[clazz]
	}

	class Items(modID: String) : RegistryProvider<Item>(BuiltInRegistries.ITEM, modID) {
		override fun <I : Item> createRegistryObject(name: String): RegistryItem<I> = RegistryItem.create(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <I : Item> register(name: String, supplier: Supplier<Item>): RegistryItem<I> = super.register<Item>(name, supplier) as RegistryItem<I>
		fun <I : Item> simpleItem(name: String, properties: Item.Properties): RegistryItem<I> = this.register(name) { Item(properties) }
		fun <B : BlockItem> registerSimpleBlockItem(
			name: String,
			block: Supplier<Block>,
			properties: Item.Properties
		): RegistryItem<B> = this.register(name) { BlockItem(block.get(), properties) }
	}
}
