package org.bread_experts_group.breadlib.data

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadlib.registry.objects.AbstractRegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.util.resolve
import java.util.*
import java.util.concurrent.CompletableFuture

class LocaleGenerator(override val modID: String, private val locale: Locale) : DataGenerator() {
	private val translations = mutableMapOf<String, String>()

	fun add(vararg translations: Pair<String, String>) {
		for ((key, value) in translations) this.translations.put(key, value)?.let {
			throw IllegalArgumentException(
				"Duplicate translation key \"$key\" while trying to add \"$value\": existed as \"$it\""
			)
		}
	}

	fun addItems(vararg translations: Pair<Item, String>): Unit = this.add(
		*translations.map { (item, value) -> item.descriptionId to value }.toTypedArray()
	)

	fun addBlocks(vararg translations: Pair<Block, String>): Unit = this.add(
		*translations.map { (block, value) -> block.descriptionId to value }.toTypedArray()
	)

	fun addBLItems(vararg translations: Pair<RegistryItem<*>, String>) {
		this.addItems(*translations.map { (first, second) -> first.get() to second }.toTypedArray())
	}

	fun addBLBlocks(vararg translations: Pair<AbstractRegistryBlock<*>, String>) {
		this.addBlocks(*translations.map { (first, second) -> first.get() to second }.toTypedArray())
	}

	override fun getName(): String = "BreadLib LocaleGenerator ($modID, ${locale.country}, ${locale.language})"

	override fun run(p0: CachedOutput): CompletableFuture<*> {
		if (this.translations.isEmpty()) return CompletableFuture.completedFuture(null)
		require(!(locale.language.isEmpty() || locale.country.isEmpty())) { "Please use a Locale with a country and language set." }
		return DataProvider.saveStable(
			p0,
			JsonObject().also {
				translations.forEach { (key, value) -> it.addProperty(key, value) }
			},
			this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(
				modID,
				"lang", "${locale.language}_${locale.country.lowercase()}.json"
			)
		)
	}
}