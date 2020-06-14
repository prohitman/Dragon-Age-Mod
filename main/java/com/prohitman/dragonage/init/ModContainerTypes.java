package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.containers.ForgingTableContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ModContainerTypes 
{
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(
			ForgeRegistries.CONTAINERS, DragonAge.MOD_ID);

	public static final RegistryObject<ContainerType<ForgingTableContainer>> FORGING_TABLE_CONTAINER = CONTAINER_TYPES
			.register("forging_table", () -> IForgeContainerType.create(ForgingTableContainer::new));

}
