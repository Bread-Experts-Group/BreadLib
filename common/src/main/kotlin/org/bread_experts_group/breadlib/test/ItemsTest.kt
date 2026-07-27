package org.bread_experts_group.breadlib.test

import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getItems
import org.bread_experts_group.breadlib.registry.objects.RegistryItem

object ItemsTest {
	val REGISTRY: RegistryProvider.Items = getItems(BreadLib.MOD_ID)

	val TEST_ITEM: RegistryItem<TestItem> = REGISTRY.register("test_item", ::TestItem)
}