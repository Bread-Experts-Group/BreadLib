package org.bread_experts_group.breadlib.rendering.model

import com.mojang.math.Transformation
import net.minecraft.client.renderer.FaceInfo
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockElementRotation
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.abs
import kotlin.math.cos

object ModelUtil {
    fun Collection<BakedQuad>.model(): BakedModel = object : BakedModel {
        override fun getQuads(state: BlockState?, direction: Direction?, random: RandomSource): List<BakedQuad> = this@model.toList()
        override fun useAmbientOcclusion(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isGui3d(): Boolean {
            TODO("Not yet implemented")
        }

        override fun usesBlockLight(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCustomRenderer(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getParticleIcon(): TextureAtlasSprite {
            TODO("Not yet implemented")
        }

        override fun getTransforms(): ItemTransforms {
            TODO("Not yet implemented")
        }

        override fun getOverrides(): ItemOverrides {
            TODO("Not yet implemented")
        }
    }

    private fun rotateVertexBy(pos: Vector3f, origin: Vector3f, transform: Matrix4f, scale: Vector3f) {
        val vector4f =
            transform.transform(Vector4f(pos.x() - origin.x(), pos.y() - origin.y(), pos.z() - origin.z(), 1.0f))
        vector4f.mul(Vector4f(scale, 1.0f))
        pos.set(vector4f.x() + origin.x(), vector4f.y() + origin.y(), vector4f.z() + origin.z())
    }

    private val RESCALE_22_5 = 1.0f / cos((Math.PI / 8).toFloat().toDouble()).toFloat() - 1.0f
    private val RESCALE_45 = 1.0f / cos((Math.PI / 4).toFloat().toDouble()).toFloat() - 1.0f

    private fun applyElementRotation(vec: Vector3f, partRotation: BlockElementRotation?) {
        if (partRotation != null) {
            val vector3f: Vector3f?
            val vector3f1: Vector3f?
            when (partRotation.axis()) {
                Direction.Axis.X -> {
                    vector3f = Vector3f(1.0f, 0.0f, 0.0f)
                    vector3f1 = Vector3f(0.0f, 1.0f, 1.0f)
                }

                Direction.Axis.Y -> {
                    vector3f = Vector3f(0.0f, 1.0f, 0.0f)
                    vector3f1 = Vector3f(1.0f, 0.0f, 1.0f)
                }

                Direction.Axis.Z -> {
                    vector3f = Vector3f(0.0f, 0.0f, 1.0f)
                    vector3f1 = Vector3f(1.0f, 1.0f, 0.0f)
                }

                else -> throw IllegalArgumentException("There are only 3 axes")
            }

            val quaternion = Quaternionf().rotationAxis(partRotation.angle() * (Math.PI / 180.0).toFloat(), vector3f)
            if (partRotation.rescale()) {
                if (abs(partRotation.angle()) == 22.5f) {
                    vector3f1.mul(RESCALE_22_5)
                } else {
                    vector3f1.mul(RESCALE_45)
                }

                vector3f1.add(1.0f, 1.0f, 1.0f)
            } else {
                vector3f1.set(1.0f, 1.0f, 1.0f)
            }

            this.rotateVertexBy(vec, Vector3f(partRotation.origin()), Matrix4f().rotation(quaternion), vector3f1)
        }
    }

    fun applyModelRotation(pos: Vector3f, transform: Transformation) {
        if (transform !== Transformation.identity()) this.rotateVertexBy(
            pos,
            Vector3f(0.5f, 0.5f, 0.5f),
            transform.matrix,
            Vector3f(1.0f, 1.0f, 1.0f)
        )
    }

    private fun fillVertex(
        vertexData: IntArray,
        vertexIndex: Int,
        vector: Vector3f,
        sprite: TextureAtlasSprite,
        blockFaceUV: BlockFaceUV
    ) {
        val i = vertexIndex * 8
        vertexData[i] = java.lang.Float.floatToRawIntBits(vector.x())
        vertexData[i + 1] = java.lang.Float.floatToRawIntBits(vector.y())
        vertexData[i + 2] = java.lang.Float.floatToRawIntBits(vector.z())
        vertexData[i + 3] = -1
        vertexData[i + 4] = java.lang.Float.floatToRawIntBits(sprite.getU(blockFaceUV.getU(vertexIndex) / 16.0f))
        vertexData[i + 4 + 1] = java.lang.Float.floatToRawIntBits(sprite.getV(blockFaceUV.getV(vertexIndex) / 16.0f))
    }

    private fun bakeVertex(
        vertexData: IntArray,
        vertexIndex: Int,
        facing: Direction,
        blockFaceUV: BlockFaceUV,
        posDiv16: FloatArray,
        sprite: TextureAtlasSprite,
        rotation: Transformation,
        partRotation: BlockElementRotation?
    ) {
        val vertexInfo = FaceInfo.fromFacing(facing).getVertexInfo(vertexIndex)
        val vector3f = Vector3f(
            posDiv16[vertexInfo.xFace],
            posDiv16[vertexInfo.yFace],
            posDiv16[vertexInfo.zFace]
        )
        this.applyElementRotation(vector3f, partRotation)
        this.applyModelRotation(vector3f, rotation)
        this.fillVertex(vertexData, vertexIndex, vector3f, sprite, blockFaceUV)
    }

    fun makeVertices(
        blockFaceUV: BlockFaceUV,
        sprite: TextureAtlasSprite,
        facing: Direction,
        posDiv16: FloatArray,
        rotation: Transformation,
        partRotation: BlockElementRotation?
    ): IntArray = IntArray(32).also { vertexData ->
        for (vertexIndex in 0..3) this.bakeVertex(
            vertexData, vertexIndex,
            facing, blockFaceUV, posDiv16, sprite, rotation, partRotation
        )
    }

    fun setupShape(min: Vector3f, max: Vector3f): FloatArray = FloatArray(Direction.entries.size).also { shape ->
        shape[FaceInfo.Constants.MIN_X] = min.x() / 16.0f
        shape[FaceInfo.Constants.MIN_Y] = min.y() / 16.0f
        shape[FaceInfo.Constants.MIN_Z] = min.z() / 16.0f
        shape[FaceInfo.Constants.MAX_X] = max.x() / 16.0f
        shape[FaceInfo.Constants.MAX_Y] = max.y() / 16.0f
        shape[FaceInfo.Constants.MAX_Z] = max.z() / 16.0f
    }
}