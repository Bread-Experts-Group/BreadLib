package org.bread_experts_group.breadlib.data

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadlib.registry.objects.AbstractRegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadlib.util.resolve
import java.util.*
import java.util.concurrent.CompletableFuture

class LocaleProvider(
	private val locale: Locale,
	modID: String,
	packOutput: PackOutput
) : DataGenerationProvider(modID, packOutput) {
	private val translations = mutableMapOf<String, String>()

	fun add(vararg translations: Pair<String, String>): LocaleProvider = this.also {
		for ((key, value) in translations) this.translations.put(key, value)?.let {
			throw IllegalArgumentException(
				"Duplicate translation key \"$key\" while trying to add \"$value\": existed as \"$it\""
			)
		}
	}

	fun addCreativeTabs(vararg translations: Pair<CreativeModeTab, String>): LocaleProvider = this.add(
		*translations.map { (tab, value) ->  tab.displayName.string to value}.toTypedArray()
	)

	fun addBLCreativeTabs(vararg translations: Pair<RegistryObject<*, CreativeModeTab>, String>): LocaleProvider =
		this.addCreativeTabs(*translations.map { (tab, value) ->  tab.get() to value}.toTypedArray())

	fun addItems(vararg translations: Pair<Item, String>): LocaleProvider = this.add(
		*translations.map { (item, value) -> item.descriptionId to value }.toTypedArray()
	)

	fun addBlocks(vararg translations: Pair<Block, String>): LocaleProvider = this.add(
		*translations.map { (block, value) -> block.descriptionId to value }.toTypedArray()
	)

	fun addBLItems(vararg translations: Pair<RegistryItem<*>, String>): LocaleProvider = this.addItems(
		*translations.map { (first, second) -> first.get() to second }.toTypedArray()
	)

	fun addBLBlocks(vararg translations: Pair<AbstractRegistryBlock<*>, String>): LocaleProvider = this.addBlocks(
		*translations.map { (first, second) -> first.get() to second }.toTypedArray()
	)

	override fun getName(): String = "BreadLib LocaleProvider ($modID, ${locale.country}, ${locale.language})"

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