package org.bread_experts_group.breadlib.data

import com.google.common.hash.Hashing
import com.google.common.hash.HashingOutputStream
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadlib.registry.objects.AbstractRegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.util.location
import org.bread_experts_group.breadlib.util.resolve
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.io.path.createDirectories

class PlaceholderTextureGenerator(override val modID: String) : DataGenerator() {
	private val checkerboards = mutableMapOf<ResourceLocation, String>()
	fun checkerboard(location: ResourceLocation, category: String = "item") {
		require(checkerboards.put(location, category) == null) { "$location already defined for texture generation ($category, checkerboard)" }
	}

	fun checkerboard(item: Item): Unit = checkerboard(item.location)
	fun checkerboard(block: Block): Unit = checkerboard(block.location, "block")

	fun checkerboard(item: RegistryItem<*>): Unit = checkerboard(item.get())
	fun checkerboard(block: AbstractRegistryBlock<*>): Unit = checkerboard(block.get())

	override fun getName(): String = "BreadLib Placeholder Texture Generator ($modID)"

	@Suppress("UnstableApiUsage")
	override fun run(p0: CachedOutput): CompletableFuture<*> {
		val completableFutures = arrayOfNulls<CompletableFuture<*>>(checkerboards.size)
		var i = 0
		for ((location, category) in checkerboards) {
			val output = packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(
				location.namespace, "textures", category, "${location.path}.png"
			)
			completableFutures[i++] = CompletableFuture.runAsync {
				output.parent.createDirectories()
				val image = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB)
				image.createGraphics().apply {
					fun createColor(hash: Int) = Color(
						Color.HSBtoRGB(
							(hash / 0xFF.toFloat()) * 360,
							0.75f,
							0.90f
						)
					)

					var i = 0
					for (x in 0..<8 step 4) {
						for (y in 0..<8 step 4) {
							color = createColor((location.namespace.hashCode() ushr (i++ * 8)) and 0xFF)
							fillRect(x, y, 4, 4)
						}
					}

					i = 0
					for (x in 8..<16 step 4) {
						for (y in 8..<16 step 4) {
							color = createColor((location.path.hashCode() ushr (i++ * 8)) and 0xFF)
							fillRect(x, y, 4, 4)
						}
					}
					dispose()
				}
				val imageOutput = ByteArrayOutputStream(1024)
				val hashOutput = HashingOutputStream(Hashing.sha1(), imageOutput)
				ImageIO.write(image, "png", hashOutput)
				p0.writeIfNeeded(output, imageOutput.toByteArray(), hashOutput.hash())
			}
		}
		return CompletableFuture.allOf(*completableFutures)
	}
}