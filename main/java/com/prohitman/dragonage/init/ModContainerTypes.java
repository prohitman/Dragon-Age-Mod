package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.containers.ForgeringTableContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ModContainerTypes 
{
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(
			ForgeRegistries.CONTAINERS, DragonAge.MOD_ID);

	public static final RegistryObject<ContainerType<ForgeringTableContainer>> FORGERING_TABLE_CONTAINER = CONTAINER_TYPES
			.register("forgering_table", () -> IForgeContainerType.create(ForgeringTableContainer::new));

}
