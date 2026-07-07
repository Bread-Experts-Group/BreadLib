package org.bread_experts_group.breadlib.task;

import com.mojang.brigadier.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.extensions.item.IKeyboardItem;
import org.bread_experts_group.breadlib.extensions.item.IMouseItem;
import org.bread_experts_group.breadlib.task.command.ServerCommandTask;
import org.bread_experts_group.breadlib.task.input.KeyboardTask;
import org.bread_experts_group.breadlib.task.input.MouseTasks;

public class BreadLibTasks {
	public static void setupInputTasks() {
		TaskManager.newTask(MouseTasks.Scroll.class, task -> {
			Minecraft minecraft = Minecraft.getInstance();
			Player player = minecraft.player;
			if (player == null) return;
			ItemStack heldStack = player.getMainHandItem();
			if (heldStack.getItem() instanceof IMouseItem item) {
				if (item.onMouseScroll(heldStack, (ClientLevel) player.level(), player)) task.cancel();
			}
		});

		TaskManager.newTask(MouseTasks.Button.class, task -> {
			Minecraft minecraft = Minecraft.getInstance();
			Player player = minecraft.player;
			if (player == null) return;
			ItemStack heldStack = player.getMainHandItem();
			if (heldStack.getItem() instanceof IMouseItem item) {
				if (task.isPre()) {
					if (item.onMouseInputPre(heldStack, (ClientLevel) player.level(), player)) task.cancel();
				} else if (task.isPost()) item.onMouseInputPost(heldStack, (ClientLevel) player.level(), player);
			}
		});

		TaskManager.newTask(KeyboardTask.class, task -> {
			Minecraft minecraft = Minecraft.getInstance();
			Player player = minecraft.player;
			if (player == null) return;
			ItemStack heldStack = player.getMainHandItem();
			if (heldStack.getItem() instanceof IKeyboardItem item) {
				item.onKeyPress(task.button, task.scanCode, task.action, task.modifiers, heldStack, (ClientLevel) player.level(), player);
			}
		});
	}

	public static void setupCommandTasks() {
		TaskManager.newTask(ServerCommandTask.class, task ->
				task.registerCommand((dispatcher, context) -> dispatcher.register(
						Commands.literal(BreadLib.MOD_ID)
								.then(Commands.literal("test")
										.executes((subContext) -> {
											System.out.println("test");
											return Command.SINGLE_SUCCESS;
										})
								)
				))
		);
	}
}