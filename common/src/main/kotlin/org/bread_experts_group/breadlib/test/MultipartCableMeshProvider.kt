package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockElementFace
import net.minecraft.client.renderer.block.model.BlockElementRotation
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.rendering.model.MeshProvider
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.makeVertices
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.model
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.setupShape
import org.bread_experts_group.breadlib.util.minecraft
import org.joml.Vector3f

object MultipartCableMeshProvider : MeshProvider {
    override fun invoke(
        state: BlockState,
        pos: BlockPos,
        level: Level,
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        randomSource: RandomSource
    ) {
        BreadLib.LOGGER.info("CableBlock Custom Render: $pos, ${state.block}")

        val sprite = minecraft!!.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
            ResourceLocation.withDefaultNamespace("block/dirt")
        )

        val a = listOf(
            BakedQuad(
                makeVertices(
                    BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0),
                    sprite,
                    Direction.UP,
                    setupShape(Vector3f(0f, 0f, 0f), Vector3f(16f, 16f, 16f)),
                    BlockModelRotation.X0_Y0.rotation,
                    BlockElementRotation(Vector3f(), Direction.Axis.X, 0f, false)
                ),
                BlockElementFace.NO_TINT, Direction.UP,
                sprite,
                true
            )
        ).model()
        minecraft!!.blockRenderer.modelRenderer.tesselateWithAO(
            level, a, state, pos, poseStack, vertexConsumer, true, randomSource, 0,
            OverlayTexture.NO_OVERLAY
        )
    }
}