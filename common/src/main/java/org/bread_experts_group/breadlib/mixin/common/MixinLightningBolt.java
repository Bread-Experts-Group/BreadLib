package org.bread_experts_group.breadlib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadlib.extensions.ILightningStrikeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class MixinLightningBolt extends Entity {
	public MixinLightningBolt(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "powerLightningRod", at = @At("TAIL"))
	private void breadlib$strikeLightningBlock(CallbackInfo ci, @Local BlockState blockState, @Local BlockPos blockPos) {
		if (blockState.getBlock() instanceof ILightningStrikeAction lightningBlock) {
			lightningBlock.onLightningStruck(this.level(), blockPos, blockState);
		}
	}
}
