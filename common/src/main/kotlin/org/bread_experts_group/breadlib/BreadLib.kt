package org.bread_experts_group.breadlib

import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.*
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntityCapabilitiesSynchronizationPacket
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getProvider
import org.bread_experts_group.breadlib.registry.network.BiomeRegisterPacket
import org.bread_experts_group.breadlib.registry.network.DimensionTypeRegisterPacket
import org.bread_experts_group.breadlib.task.TaskManager.newTask
import org.bread_experts_group.breadlib.task.client.ClientLogInEvent
import org.bread_experts_group.breadlib.task.network.NetworkTask
import org.bread_experts_group.breadlib.task.server.ServerStartingTask
import org.bread_experts_group.breadlib.util.DimUtil.createAndRegisterWorldAndDimension
import org.bread_experts_group.breadlib.util.DimUtil.dynamicLevelDataFile
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.extension
import kotlin.io.path.walk

object BreadLib {
	const val MOD_ID: String = "breadlib"
	const val MOD_VERSION: String = "1.3.2"

	@JvmField
	val LOGGER: Logger = LogManager.getLogger("BreadLib")

	val CONFIG: Path by lazy {
		PlatformServices.PLATFORM.configDir.resolve(
			MOD_ID
		)
	}

	@JvmStatic
	fun modLoc(vararg path: String): ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path.joinToString("/"))
	}

	@JvmStatic
	fun init() {
		LOGGER.info(
			"Hello from Common init on {}! we are currently in a {} environment on the {}!",
			PlatformServices.PLATFORM.platformName,
			PlatformServices.PLATFORM.environmentKind,
			PlatformServices.PLATFORM.side
		)

		newTask { task: NetworkTask ->
			task.addClientbound(
				BreadLibBlockEntityCapabilitiesSynchronizationPacket::class.java,
				BreadLibBlockEntityCapabilitiesSynchronizationPacket.TYPE,
				BreadLibBlockEntityCapabilitiesSynchronizationPacket.STREAM_CODEC,
				BreadLibBlockEntityCapabilitiesSynchronizationPacket::handleClientbound
			)

			task.addClientbound(
				DimensionTypeRegisterPacket::class.java,
				DimensionTypeRegisterPacket.TYPE,
				DimensionTypeRegisterPacket.STREAM_CODEC,
				DimensionTypeRegisterPacket::handleClientbound
			)
			task.addClientbound(
				BiomeRegisterPacket::class.java,
				BiomeRegisterPacket.TYPE,
				BiomeRegisterPacket.STREAM_CODEC,
				BiomeRegisterPacket::handleClientbound
			)
		}

		newTask { _: ClientLogInEvent ->
			val network = PlatformServices.NETWORK
			network.trueSide.set(ApplicationSide.CLIENT)
			network.trueClient.set(Minecraft.getInstance())
		}

		newTask { task: ServerStartingTask ->
			val network = PlatformServices.NETWORK
			network.trueSide.set(ApplicationSide.SERVER)
			network.trueServer.set(task.server)

			fun register(nbt: CompoundTag, form: Int ) {
				val dataTag = nbt.get("data")
				val data = when (form) {
					0 -> Biome.DIRECT_CODEC.decode(NbtOps.INSTANCE, dataTag)
					1 -> DimensionType.DIRECT_CODEC.decode(NbtOps.INSTANCE, dataTag)
					2 -> LevelStem.CODEC.decode(
						RegistryOps.create(NbtOps.INSTANCE, task.server.registryAccess()),
						dataTag
					)
					3 -> NoiseGeneratorSettings.DIRECT_CODEC.decode(
						RegistryOps.create(NbtOps.INSTANCE, task.server.registryAccess()),
						dataTag
					)

					else -> throw NotImplementedError("Codec form $form")
				}.orThrow.first

				val parentKey = ResourceLocation.parse(nbt.getString("registry_registry"))
				val registryKey = ResourceLocation.parse(nbt.getString("registry"))
				val itemKey = ResourceLocation.parse(nbt.getString("item"))

				val registry = task.server.registryAccess().registry(
					ResourceKey.create(
						ResourceKey.createRegistryKey<Registry<*>>(parentKey),
						registryKey
					)
				).get().getProvider(itemKey.namespace)

				registry.freeze()
				registry.register<Any>(itemKey.path, false) { data }
			}

			RegistryProvider.DYNAMICS.walk().filter {
				it.extension.lowercase() == "nbtc"
			}.map {
				val nbt = NbtIo.readCompressed(it, NbtAccounter.unlimitedHeap())
				nbt to nbt.getInt("codec_form")
			}.filter { (nbt, form) ->
				if (form == 0 || form == 1) {
					register(nbt, form)
					return@filter false
				}
				return@filter true
			}.forEach { (nbt, form) ->
				register(nbt, form)
			}

			val dynLevels = try {
				NbtIo.readCompressed(
					task.server.dynamicLevelDataFile().also { it.parent.createDirectories() },
					NbtAccounter.unlimitedHeap()
				)
			} catch (_: IOException) {
				CompoundTag()
			}
			dynLevels.getList("levels_simple", Tag.TAG_STRING.toInt()).forEach {
				val registry = task.server.registryAccess()
				val location = ResourceLocation.parse(it.asString)

				val dimensionKey: ResourceLocation = location
				val levelStem: LevelStem = registry.registry(Registries.LEVEL_STEM).get()[location]!!

				createAndRegisterWorldAndDimension(dimensionKey, levelStem, false)
			}
			dynLevels.getList("levels", Tag.TAG_LIST.toInt()).forEach {
				it as ListTag

				val dimensionKey: ResourceLocation = ResourceLocation.parse((it[0] as StringTag).asString)

				val registry = task.server.registryAccess()
				val stemKeyLocation = ResourceLocation.parse((it[1] as StringTag).asString)
				val levelStem: LevelStem = registry.registry(Registries.LEVEL_STEM).get()[stemKeyLocation]!!

				createAndRegisterWorldAndDimension(dimensionKey, levelStem, false)
			}
		}

		kExample()

		LOGGER.info(PlatformServices.PLATFORM.getModInfo("breadlib").hash)
	}
}