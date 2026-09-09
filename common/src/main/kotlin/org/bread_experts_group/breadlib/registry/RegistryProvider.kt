package org.bread_experts_group.breadlib.registry

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadlib.util.DimUtil.register
import org.jetbrains.annotations.ApiStatus
import java.nio.file.Path
import java.util.function.Supplier
import kotlin.io.path.createDirectories

open class RegistryProvider<T> private constructor(
	val registry: Registry<T>,
	val modID: String
) {
	companion object {
		val providers: MutableMap<String, MutableMap<Registry<*>, RegistryProvider<*>>> = mutableMapOf()

		/**
		 * Method for statically initializing the classes holding providers and their contents.
		 */
		@Suppress("unused")
		fun initialize(vararg providers: RegistryProvider<*>): Unit = Unit

		fun getBlocks(modID: String): Blocks = BuiltInRegistries.BLOCK.getProvider(modID) as Blocks
		fun getBlockEntityTypes(modID: String): BlockEntityTypes =
			BuiltInRegistries.BLOCK_ENTITY_TYPE.getProvider(modID) as BlockEntityTypes

		fun getItems(modID: String): Items = BuiltInRegistries.ITEM.getProvider(modID) as Items

		@Suppress("UNCHECKED_CAST")
		fun <T> Registry<T>.getProvider(modID: String): RegistryProvider<T> = providers.getOrPut(
			modID
		) { mutableMapOf() }.getOrPut(this) {
			when (this) {
				BuiltInRegistries.BLOCK -> Blocks(modID)
				BuiltInRegistries.ITEM -> Items(modID)
				BuiltInRegistries.BLOCK_ENTITY_TYPE -> BlockEntityTypes(modID)
				else -> RegistryProvider(this, modID)
			}
		} as RegistryProvider<T>

		val DYNAMICS = BreadLib.CONFIG.resolve("registry dynamics")
	}

	val entries: MutableMap<RegistryObject<T, out T>, Supplier<T>> = mutableMapOf()
	val key: ResourceKey<Registry<T>> = ResourceKey.createRegistryKey(this.registry.key().location())
	var frozen: Boolean = false
		private set

	@ApiStatus.Internal
	fun freeze() {
		this.frozen = true
	}

	open fun <I : T> createRegistryObject(name: String): RegistryObject<T, I> = RegistryObject.create(
		this.modID, name,
		this.registry
	)

	fun <I : T> getOrRegister(
		name: String, persistDynamic: Boolean = true,
		supplier: Supplier<T>
	): RegistryObject<T, I> {
		val location = ResourceLocation.fromNamespaceAndPath(modID, name)
		if (registry.containsKey(location)) {
			val regObject = this.createRegistryObject<I>(name)
			regObject.bind()
			return regObject
		}
		return register(name, persistDynamic, supplier)
	}

	val dynamicsFolder: Path = DYNAMICS.resolve(modID)
	open fun <I : T> register(
		name: String, persistDynamic: Boolean = true,
		supplier: Supplier<T>
	): RegistryObject<T, I> {
		val location = ResourceLocation.fromNamespaceAndPath(modID, name)
		check(!registry.containsKey(location)) {
			"Duplicate underlying entry: $location"
		}
		val regObject = this.createRegistryObject<I>(name)
		check(this.entries.putIfAbsent(regObject, supplier) == null) {
			"Duplicate registry entry: $location"
		}
		if (frozen) {
			if (PlatformServices.NETWORK.side == ApplicationSide.CLIENT) throw IllegalStateException(
				"The client cannot dynamically add to the registry."
			)
			val registries = PlatformServices.NETWORK.server.registryAccess()
			@Suppress("UNCHECKED_CAST")
			registries.register(
				registry.key() as ResourceKey<Registry<Any>>,
				{ supplier.get() as Any }, location
			)
			regObject.bind()

			if (persistDynamic) {
				val (saved, form) = when (val encodingValue = regObject.get()) {
					is Biome -> Biome.DIRECT_CODEC.encode(encodingValue, NbtOps.INSTANCE, null) to 0
					is DimensionType -> DimensionType.DIRECT_CODEC.encode(encodingValue, NbtOps.INSTANCE, null) to 1
					is LevelStem -> LevelStem.CODEC.encode(
						encodingValue,
						RegistryOps.create(NbtOps.INSTANCE, registries), CompoundTag()
					) to 2
					is NoiseGeneratorSettings -> NoiseGeneratorSettings.DIRECT_CODEC.encode(
						encodingValue,
						RegistryOps.create(NbtOps.INSTANCE, registries), CompoundTag()
					) to 3
					else -> throw IllegalStateException("Persistent dynamic registration not available for $encodingValue")
				}

				val data = CompoundTag().apply {
					put("data", saved.orThrow)

					val registryKey = registry.key()
					putString(
						"registry_registry",
						registryKey.registry().toString()
					)
					putString(
						"registry",
						registryKey.location().toString()
					)
					putString(
						"item",
						"$modID:$name"
					)
					putInt(
						"codec_form",
						form
					)
				}

				NbtIo.writeCompressed(
					data,
					dynamicsFolder
						.createDirectories()
						.resolve("${System.currentTimeMillis()}_${System.nanoTime()}.nbtc")
				)
			}
		}
		return regObject
	}

	class Blocks(modID: String) : RegistryProvider<Block>(BuiltInRegistries.BLOCK, modID) {
		override fun <B : Block> createRegistryObject(name: String): RegistryBlock<B> =
			RegistryBlock.create(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <B : Block> register(name: String, persistDynamic: Boolean, supplier: Supplier<Block>): RegistryBlock<B> =
			super.register<Block>(name, persistDynamic, supplier) as RegistryBlock<B>

		fun <B : Block> registerSimpleBlock(name: String, properties: BlockBehaviour.Properties): RegistryBlock<B> =
			this.register(name) { Block(properties) }
	}

	class BlockEntityTypes(modID: String) :
		RegistryProvider<BlockEntityType<*>>(BuiltInRegistries.BLOCK_ENTITY_TYPE, modID) {
		inner class BlockEntityTypeBuilder<T : BlockEntity>(
			factory: BlockEntitySupplier<T>,
			validBlocks: Set<Block>
		) : BlockEntityType<T>(factory, validBlocks, null) {
			fun withRenderer(provider: BlockEntityRendererProvider<T>): BlockEntityTypeBuilder<T> = this.also {
				BlockEntityRenderers.register(this, provider)
			}
		}

		private val types = mutableMapOf<Class<*>, BlockEntityType<*>>()

		@get:ApiStatus.Internal
		val applicableBlocks: List<BreadLibBlockWithEntity<*>> by lazy {
			getBlocks(modID)
				.also { if (!it.frozen) it.freeze() }
				.entries.keys
				.mapNotNull { it.get() as? BreadLibBlockWithEntity<*> }
		}

		inline fun <reified T : BlockEntity> create(
			noinline factory: (pos: BlockPos, state: BlockState) -> T
		): BlockEntityTypeBuilder<T> = this.create(T::class.java, factory)

		@ApiStatus.Internal
		fun <T : BlockEntity> create(
			returnClass: Class<T>,
			factory: (pos: BlockPos, state: BlockState) -> T
		): BlockEntityTypeBuilder<T> {
			val validBlocks = mutableSetOf<Block>()
			this.applicableBlocks.filterTo(validBlocks) { returnClass == it.blockEntity }
			val builder = BlockEntityTypeBuilder(factory, validBlocks)
			this.types[returnClass] = builder
			return builder
		}

		fun <I : BlockEntityType<*>> register(
			name: String, persistDynamic: Boolean,
			supplier: BlockEntityTypes.() -> BlockEntityType<*>
		): RegistryObject<BlockEntityType<*>, I> {
			return super.register(name, persistDynamic) { this.supplier() }
		}

		@ApiStatus.Internal
		fun getType(clazz: Class<*>): BlockEntityType<*>? = this.types[clazz]
	}

	class Items(modID: String) : RegistryProvider<Item>(BuiltInRegistries.ITEM, modID) {
		override fun <I : Item> createRegistryObject(name: String): RegistryItem<I> =
			RegistryItem.create(this.modID, name)

		@Suppress("UNCHECKED_CAST")
		override fun <I : Item> register(name: String, persistDynamic: Boolean, supplier: Supplier<Item>): RegistryItem<I> =
			super.register<Item>(name, persistDynamic, supplier) as RegistryItem<I>

		fun <I : Item> simpleItem(name: String, properties: Item.Properties): RegistryItem<I> =
			this.register(name) { Item(properties) }

		fun registerSimpleBlockItem(
			name: String,
			block: Supplier<Block>,
			properties: Item.Properties
		): RegistryItem<BlockItem> = this.register(name) { BlockItem(block.get(), properties) }
	}
}
