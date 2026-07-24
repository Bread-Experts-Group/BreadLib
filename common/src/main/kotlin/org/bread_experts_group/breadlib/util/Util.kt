@file:Suppress("RedundantValueArgument", "unused")

package org.bread_experts_group.breadlib.util

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.util.Supplier
import org.bread_experts_group.breadlib.BreadLib
import java.nio.file.Path

fun getFrame(backwardsDepth: Long): StackWalker.StackFrame? = StackWalker.getInstance(
	StackWalker.Option.RETAIN_CLASS_REFERENCE
).walk { frames ->
	frames
		.skip(1 + backwardsDepth)
		.findFirst()
}.orElse(null)

fun log(level: Level, backwardsDepth: Long = 0, message: () -> Any?) {
	val callerFrame = getFrame(1 + backwardsDepth) ?: return BreadLib.LOGGER.log(level, message)
	val lineStr = if (callerFrame.isNativeMethod) "native"
	else if (callerFrame.lineNumber < 0) "unknown"
	else callerFrame.lineNumber
	val msg = Supplier {
		"${callerFrame.fileName}: ${callerFrame.className.substringAfterLast('.')}.${callerFrame.methodName}:" +
				"$lineStr: " + message()
	}
	BreadLib.LOGGER.log(level, msg)
}

fun trace(backwardsDepth: Long = 1, message: () -> Any?): Unit = log(Level.TRACE, 1 + backwardsDepth, message)
fun trace(message: Any?): Unit = trace(1) { message }

fun debug(backwardsDepth: Long = 1, message: () -> Any?): Unit = log(Level.DEBUG, 1 + backwardsDepth, message)
fun debug(message: Any?): Unit = debug(1) { message }

fun info(backwardsDepth: Long = 1, message: () -> Any?): Unit = log(Level.INFO, 1 + backwardsDepth, message)
fun info(message: Any?): Unit = info(1) { message }

fun warn(backwardsDepth: Long = 1, message: () -> Any?): Unit = log(Level.WARN, 1 + backwardsDepth, message)
fun warn(message: Any?): Unit = warn(1) { message }

fun fatal(backwardsDepth: Long = 1, message: () -> Any?): Unit = log(Level.FATAL, 1 + backwardsDepth, message)
fun fatal(message: Any?): Unit = fatal(1) { message }

fun Path.resolve(vararg paths: String): Path = paths.fold(this) { a, path -> a.resolve(path) }

val Item.location: ResourceLocation
	get() = BuiltInRegistries.ITEM.getKey(this)
val Block.location: ResourceLocation
	get() = BuiltInRegistries.BLOCK.getKey(this)