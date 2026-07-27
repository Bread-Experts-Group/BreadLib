package org.bread_experts_group.breadlib.test

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.get
import org.bread_experts_group.breadlib.registry.objects.RegistryObject

object CreativeTabTest {
	val REGISTRY: RegistryProvider<CreativeModeTab> = BuiltInRegistries.CREATIVE_MODE_TAB.get(BreadLib.MOD_ID)

	var TEST_TAB: RegistryObject<CreativeModeTab, CreativeModeTab> = REGISTRY.register("test_tab") {
		CreativeModeTab
			.builder(CreativeModeTab.Row.TOP, 1)
			.title(Component.literal("BreadLib"))
			.icon { ItemsTest.TEST_ITEM.toStack() }
			.displayItems { _, output ->
				ItemsTest.REGISTRY.entries.keys.forEach { entry -> output.accept(entry.get()) }
			}
			.build()
	}
}
