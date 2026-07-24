package org.bread_experts_group.breadlib.rendering

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.world.phys.Vec2
import org.lwjgl.system.MemoryUtil

object ExtraVertexFormatElements {
	@JvmField
	val SPEED: VertexFormatElement = VertexFormatElement.register(
		nextId(), 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1
	)

	@JvmField
	val DIRECTION: VertexFormatElement = VertexFormatElement.register(
		nextId(), 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2
	)

	private fun nextId(): Int {
		var index = 0
		for (element in VertexFormatElement.BY_ID) {
			if (element != null) index++
			else break
		}
		return index
	}

	@JvmStatic
	fun VertexConsumer.putFloat(element: VertexFormatElement, value: Float) {
		val builder = this as BufferBuilder
		val begun = builder.beginElement(element)
		if (begun != -1L) {
			MemoryUtil.memPutFloat(begun, value)
		}
	}

	@JvmStatic
	fun VertexConsumer.putVec2(element: VertexFormatElement, value: Vec2) {
		val builder = this as BufferBuilder
		val begun = builder.beginElement(element)
		if (begun != -1L) {
			MemoryUtil.memPutFloat(begun, value.x)
			MemoryUtil.memPutFloat(begun + 4L, value.y)
		}
	}
}
