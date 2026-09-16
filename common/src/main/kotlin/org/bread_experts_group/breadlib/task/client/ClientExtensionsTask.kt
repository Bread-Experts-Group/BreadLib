package org.bread_experts_group.breadlib.task.client

import net.minecraft.world.item.Item
import org.bread_experts_group.breadlib.registry.client.IClientItemExtension
import org.bread_experts_group.breadlib.task.Task

class ClientExtensionsTask : Task() {
	private val extensions: MutableList<Pair<IClientItemExtension, Item>> = mutableListOf()

	fun getExtensions(): Collection<Pair<IClientItemExtension, Item>> = this.extensions

	fun addItemExtension(extension: IClientItemExtension, item: Item) {
		this.extensions.add(extension to item)
	}
}