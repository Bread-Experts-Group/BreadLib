package org.bread_experts_group.breadlib

import org.bread_experts_group.breadlib.data.ModelGenerator
import org.bread_experts_group.breadlib.data.LocaleGenerator
import org.bread_experts_group.breadlib.data.PlaceholderTextureGenerator
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.test.*
import org.bread_experts_group.breadlib.util.info
import org.bread_experts_group.breadlib.util.location
import org.bread_experts_group.breadlib.util.newTask
import java.util.Locale

private fun kGetLocaleGenerator() = LocaleGenerator(BreadLib.MOD_ID, Locale.of("en", "us")).also {
	it.addBLBlocks(
		BlocksTest.TEST_BLOCK to "Test Block",
	)
	it.addBLItems(
		ItemsTest.TEST_ITEM to "Test Item"
	)
}

private fun kGetModelGenerator() = ModelGenerator(BreadLib.MOD_ID).also {
	it.flat2D(ItemsTest.TEST_ITEM)
}

private fun kGetPlaceholderTextureGenerator() = PlaceholderTextureGenerator(BreadLib.MOD_ID).also {
	it.checkerboard(ItemsTest.TEST_ITEM.get().location)
}

fun kExample() {
	RegistryProvider.registerAll(
		BlocksTest.BLOCK_REGISTRY,
		ItemsTest.ITEM_REGISTRY,
		CreativeTabTest.CREATIVE_TABS_REGISTRY,
		BlockEntityTypeTest.BLOCK_ENTITY_TYPE_REGISTRY
	)

//	TasksTest.renderTest();
	TasksTest.mouseTests()
	TasksTest.layeredDrawTest()
	TasksTest.networkTest()

	newTask<GenerateDataTask> { task ->
		task.addGenerator(kGetLocaleGenerator())
		task.addGenerator(kGetModelGenerator())
		task.addGenerator(kGetPlaceholderTextureGenerator())
	}

	if (PlatformServices.PLATFORM.isModLoaded("breadlib")) info("breadlib appears loaded on the platform")
}