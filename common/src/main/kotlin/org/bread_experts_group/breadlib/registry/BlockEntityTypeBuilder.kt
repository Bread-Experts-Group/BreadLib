package org.bread_experts_group.breadlib.registry

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

class BlockEntityTypeBuilder<T : BlockEntity> private constructor(private val type: BlockEntityType<T>) {
	companion object {
		@JvmStatic
		fun <T : BlockEntity> create(
			factory: BlockEntityType.BlockEntitySupplier<T>,
			vararg validBlocks: Block
		): BlockEntityTypeBuilder<T> {
			val type = BlockEntityType.Builder.of<T>(factory, *validBlocks).build(null)
			return BlockEntityTypeBuilder<T>(type)
		}
	}

	fun withRenderer(provider: BlockEntityRendererProvider<T>): BlockEntityTypeBuilder<T> {
		BlockEntityRenderers.register(this.type, provider)
		return this
	}

	fun build(): BlockEntityType<T> = type
}