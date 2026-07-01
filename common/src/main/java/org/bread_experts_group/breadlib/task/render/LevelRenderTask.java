package org.bread_experts_group.breadlib.task.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.bread_experts_group.breadlib.task.Task;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class LevelRenderTask extends Task {
	public final RenderLevelStage stage;
	public final LevelRenderer levelRenderer;
	public final PoseStack poseStack;
	public final Matrix4f projectionMatrix;
	public final DeltaTracker partialTick;
	public final Camera camera;
	public final Frustum frustum;

	public LevelRenderTask(
			RenderLevelStage stage, LevelRenderer levelRenderer, @Nullable PoseStack poseStack,
			Matrix4f projectionMatrix, DeltaTracker partialTick, Camera camera, Frustum frustum
	) {
		super();
		this.stage = stage;
		this.levelRenderer = levelRenderer;
		this.poseStack = poseStack != null ? poseStack : new PoseStack();
		this.projectionMatrix = projectionMatrix;
		this.partialTick = partialTick;
		this.camera = camera;
		this.frustum = frustum;
	}
}