package org.bread_experts_group.breadlib.test

import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock
import org.bread_experts_group.breadlib.rendering.model.MeshProvider

private val blockProperties = BlockProperties()

class MultipartCableBlock : BreadLibBlock(Properties.of()) {
	override fun breadLibProperties(): BlockProperties = blockProperties
	override val meshProvider: MeshProvider
		get() = MultipartCableMeshProvider
}