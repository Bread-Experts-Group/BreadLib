package org.bread_experts_group.breadlib.rendering

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadlib.util.Color
import org.joml.Vector3f
import org.joml.Vector4f

object VertexUtil {
	@JvmField
	val TOP_LEFT: Vector3f = Vector3f(0f, 0f, 0f)

	@JvmField
	val TOP_RIGHT: Vector3f = Vector3f(1f, 0f, 0f)

	@JvmField
	val BOTTOM_LEFT: Vector3f = Vector3f(0f, -1f, 0f)

	@JvmField
	val BOTTOM_RIGHT: Vector3f = Vector3f(1f, -1f, 0f)

	@JvmField
	val DEFAULT_NORMAL: Vector3f = Vector3f(0f, 1f, 0f)

	@JvmField
	val DEFAULT_UV: Vector4f = Vector4f(0f, 0f, 1f, 1f)

	@JvmField
	val NONE_CONSUMER: (VertexConsumer) -> Unit = {}

	@JvmStatic
	fun simpleQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		renderType: RenderType,
		color: Int,
		size: Vec2,
		extraElements: (VertexConsumer) -> Unit
	) {
		this.drawQuad(
			poseStack, bufferSource, size, renderType, color,
			this.DEFAULT_UV, this.DEFAULT_NORMAL, extraElements = extraElements
		)
	}

	@JvmStatic
	fun drawQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		size: Vec2 = Vec2(1f, 1f),
		renderType: RenderType = RenderType.debugQuads(),
		color: Int = Color.WHITE,
		uv: Vector4f = this.DEFAULT_UV,
		normal: Vector3f = this.DEFAULT_NORMAL,
		packedLight: Int = LightTexture.FULL_BRIGHT,
		packedOverlay: Int = OverlayTexture.NO_OVERLAY,
		extraElements: (VertexConsumer) -> Unit = this.NONE_CONSUMER
	) {
		val topRight = Vector3f(size.x, 0f, 0f)
		val bottomLeft = Vector3f(0f, -size.y, 0f)
		val bottomRight = Vector3f(size.x, -size.y, 0f)

		this.drawQuad(
			poseStack,
			bufferSource,
			this.TOP_LEFT,
			topRight,
			bottomLeft, bottomRight, renderType, color,
			uv,
			normal,
			packedLight,
			packedOverlay,
			extraElements
		)
	}

	@JvmStatic
	fun drawQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		topLeft: Vector3f = this.TOP_LEFT,
		topRight: Vector3f = this.TOP_RIGHT,
		bottomLeft: Vector3f = this.BOTTOM_LEFT,
		bottomRight: Vector3f = this.BOTTOM_RIGHT,
		renderType: RenderType = RenderType.debugQuads(),
		color: Int = Color.WHITE,
		uv: Vector4f = this.DEFAULT_UV,
		normal: Vector3f = this.DEFAULT_NORMAL,
		packedLight: Int = LightTexture.FULL_BRIGHT,
		packedOverlay: Int = OverlayTexture.NO_OVERLAY,
		extraElements: (VertexConsumer) -> Unit = this.NONE_CONSUMER
	) {
		this.drawVertex(
			topLeft.x, topLeft.y, topLeft.z, poseStack,
			bufferSource, renderType, color, uv.x, uv.y,
			normal, packedLight, packedOverlay, extraElements
		)
		this.drawVertex(
			bottomLeft.x, bottomRight.y, bottomLeft.z, poseStack,
			bufferSource, renderType, color, uv.x, uv.w,
			normal, packedLight, packedOverlay, extraElements
		)
		this.drawVertex(
			bottomRight.x, bottomRight.y, bottomRight.z, poseStack,
			bufferSource, renderType, color, uv.z, uv.w,
			normal, packedLight, packedOverlay, extraElements
		)
		this.drawVertex(
			topRight.x, topRight.y, topRight.z, poseStack,
			bufferSource, renderType, color, uv.z, uv.y,
			normal, packedLight, packedOverlay, extraElements
		)
	}

	@JvmStatic
	fun drawVertex(
		x: Float,
		y: Float,
		z: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		renderType: RenderType = RenderType.debugQuads(),
		color: Int = Color.WHITE,
		u: Float = 0f,
		v: Float = 0f,
		normal: Vector3f = this.DEFAULT_NORMAL,
		packedLight: Int = LightTexture.FULL_BRIGHT,
		packedOverlay: Int = OverlayTexture.NO_OVERLAY,
		extraElements: (VertexConsumer) -> Unit = this.NONE_CONSUMER
	) {
		val pose = poseStack.last().pose().transformPosition(x, y, z, Vector3f())
		this.drawVertex(
			pose.x,
			pose.y,
			pose.z,
			bufferSource,
			renderType,
			color,
			u,
			v,
			normal,
			packedLight,
			packedOverlay,
			extraElements
		)
	}

	@JvmStatic
	fun drawVertex(
		x: Float, y: Float, z: Float,
		bufferSource: MultiBufferSource,
		renderType: RenderType = RenderType.debugQuads(),
		color: Int = Color.WHITE,
		u: Float = 0f,
		v: Float = 0f,
		normal: Vector3f = this.DEFAULT_NORMAL,
		packedLight: Int = LightTexture.FULL_BRIGHT,
		packedOverlay: Int = OverlayTexture.NO_OVERLAY,
		extraElements: (VertexConsumer) -> Unit = this.NONE_CONSUMER
	) {
		val consumer = bufferSource.getBuffer(renderType)
		val vertex = consumer.addVertex(x, y, z)

		vertex.setColor(color).setUv(u, v).setOverlay(packedOverlay)
			.setLight(packedLight).setNormal(normal.x, normal.y, normal.z)
		extraElements(vertex)
	}
}
