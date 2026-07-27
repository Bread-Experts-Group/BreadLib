package org.bread_experts_group.breadlib

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadlib.data.LocaleGenerator
import org.bread_experts_group.breadlib.data.ModelGenerator
import org.bread_experts_group.breadlib.data.PlaceholderTextureGenerator
import org.bread_experts_group.breadlib.data.model.ObjectResourceLocation
import org.bread_experts_group.breadlib.data.model.block.BlockStateSingleVariant
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.initialize
import org.bread_experts_group.breadlib.task.BreadLibTasks
import org.bread_experts_group.breadlib.task.TaskManager.newTask
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.test.BlocksTest
import org.bread_experts_group.breadlib.test.CreativeTabTest
import org.bread_experts_group.breadlib.test.ItemsTest
import org.bread_experts_group.breadlib.test.client.TasksClientTest
import org.bread_experts_group.breadlib.test.server.TasksServerTest
import org.bread_experts_group.breadlib.util.info
import java.util.*

private fun kGetLocaleGenerator() = LocaleGenerator(BreadLib.MOD_ID, Locale.of("en", "us")).also {
	it.addBLBlocks(
		BlocksTest.TEST_BLOCK to "Test Block",
		BlocksTest.QUARRY to "Quarry",
	)
	it.addBLItems(
		ItemsTest.TEST_ITEM to "Test Item"
	)
}

private fun kGetModelGenerator() = ModelGenerator(BreadLib.MOD_ID).also {
	it.flat2D(ItemsTest.TEST_ITEM)

	it.flat3D(BlocksTest.TEST_BLOCK)
	it.model2D(BlocksTest.TEST_BLOCK)
	it.blockState(BlocksTest.TEST_BLOCK)

	// TODO: Work in progress texture/model
	it.verticalHorizontalFront3D(
		BlocksTest.QUARRY,
		ObjectResourceLocation(
			ResourceLocation.fromNamespaceAndPath(BreadLib.MOD_ID, "quarry_side"),
			"block"
		),
		ObjectResourceLocation(
			ResourceLocation.fromNamespaceAndPath(BreadLib.MOD_ID, "quarry_side"),
			"block"
		),
		ObjectResourceLocation(
			ResourceLocation.fromNamespaceAndPath(BreadLib.MOD_ID, "quarry_face"),
			"block"
		)
	)
	it.model2D(BlocksTest.QUARRY)
	it.blockState( // TODO: Figure out a better way than minecraft's for managing block states like facing
		BlocksTest.QUARRY,
		buildMap {
			for (direction in Direction.entries) {
				if (direction.stepY != 0) continue
				put(
					"facing=$direction",
					BlockStateSingleVariant(
						ObjectResourceLocation(BlocksTest.QUARRY),
						y = direction.opposite.toYRot()
					)
				)
			}
		}
	)
}

private fun kGetPlaceholderTextureGenerator() = PlaceholderTextureGenerator(BreadLib.MOD_ID).also {
	it.checkerboard(ItemsTest.TEST_ITEM)
	it.checkerboard(BlocksTest.TEST_BLOCK)
}

fun kExample() {
	initialize(
		BlocksTest.REGISTRY, ItemsTest.REGISTRY, CreativeTabTest.REGISTRY,
		getBlockEntityTypes(BreadLib.MOD_ID)
	)

	if (PlatformServices.PLATFORM.getSide() == ApplicationSide.CLIENT) {
		TasksClientTest.renderTest()
		TasksClientTest.layeredDrawTest()
		TasksClientTest.networkTest()

	} else {
		TasksServerTest.networkTest()
	}

	BreadLibTasks.setup()

	newTask { task: GenerateDataTask ->
		task.addGenerator(kGetLocaleGenerator())
		task.addGenerator(kGetModelGenerator())
		task.addGenerator(kGetPlaceholderTextureGenerator())
	}

	if (PlatformServices.PLATFORM.isModLoaded("breadlib")) info("breadlib appears loaded on the platform")

//	val config = ConfigManager(BreadLib.MOD_ID)["config-test", "json"]
//	val testValue =
//		ConfigValue.builder<BigDecimal>().name("test").defaultValue { BigDecimal.ONE }.comment("Test comment").build()
//	info(config.getOrNull(testValue))
//	config[testValue] = (config.getOrNull(testValue) ?: BigDecimal.ZERO).inc()
//	info(config[testValue])
//
//	val configL = ConfigManager(BreadLib.MOD_ID)["config-test", "toml"]
//	info(configL.getOrNull(testValue))
//	configL[testValue] = (configL.getOrNull(testValue) ?: BigDecimal.ZERO).inc()
//	info(configL[testValue])
}