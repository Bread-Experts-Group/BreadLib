package org.bread_experts_group.breadlib.mixin.common;

import net.minecraft.core.MappedRegistry;
import org.bread_experts_group.breadlib.extensions.IRegistryExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MappedRegistry.class)
abstract class MixinRegistry implements IRegistryExtension {
	@Shadow(remap = false) private boolean frozen;

	@Override
	public void unfreeze() {
		this.frozen = false;
	}
}
