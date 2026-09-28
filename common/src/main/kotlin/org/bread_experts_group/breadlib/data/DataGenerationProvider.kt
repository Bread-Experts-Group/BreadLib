package org.bread_experts_group.breadlib.data

import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput

abstract class DataGenerationProvider(val modID: String, val packOutput: PackOutput) : DataProvider