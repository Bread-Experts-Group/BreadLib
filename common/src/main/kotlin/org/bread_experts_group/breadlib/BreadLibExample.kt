package org.bread_experts_group.breadlib

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadlib.data.ModelGenerator
import org.bread_experts_group.breadlib.data.LocaleGenerator
import org.bread_experts_group.breadlib.data.PlaceholderTextureGenerator
import org.bread_experts_group.breadlib.data.model.ObjectResourceLocation
import org.bread_experts_group.breadlib.data.model.block.BlockStateSingleVariant
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.BreadLibTasks
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.test.*
import org.bread_experts_group.breadlib.test.client.TasksClientTest
import org.bread_experts_group.breadlib.test.server.TasksServerTest
import org.bread_experts_group.breadlib.util.info
import org.bread_experts_group.breadlib.util.newTask
import java.io.File
import java.nio.file.Files
import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.toPath

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
	RegistryProvider.registerAll(
		BlocksTest.BLOCK_REGISTRY,
		ItemsTest.ITEM_REGISTRY,
		CreativeTabTest.CREATIVE_TABS_REGISTRY,
		BlockEntityTypeTest.BLOCK_ENTITY_TYPE_REGISTRY
	)

	if (PlatformServices.PLATFORM.side == ApplicationSide.CLIENT) {
		TasksClientTest.renderTest()
		TasksClientTest.layeredDrawTest()
		TasksClientTest.networkTest()

		BreadLibTasks.setupInputTasks()
	} else {
		TasksServerTest.networkTest()
	}

	newTask<GenerateDataTask> { task ->
		task.addGenerator(kGetLocaleGenerator())
		task.addGenerator(kGetModelGenerator())
		task.addGenerator(kGetPlaceholderTextureGenerator())
	}

	if (PlatformServices.PLATFORM.isModLoaded("breadlib")) info("breadlib appears loaded on the platform")
}