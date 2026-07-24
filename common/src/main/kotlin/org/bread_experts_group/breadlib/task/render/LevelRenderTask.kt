package org.bread_experts_group.breadlib.task.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.culling.Frustum
import org.bread_experts_group.breadlib.task.Task
import org.joml.Matrix4f

class LevelRenderTask(
	val stage: RenderLevelStage, val levelRenderer: LevelRenderer, poseStack: PoseStack?,
	val projectionMatrix: Matrix4f, val partialTick: DeltaTracker, val camera: Camera, val frustum: Frustum
) : Task() {
	val poseStack: PoseStack = poseStack ?: PoseStack()
}