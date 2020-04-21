package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.tileentities.ForgeringTableTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes 
{
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, DragonAge.MOD_ID);

	public static final RegistryObject<TileEntityType<ForgeringTableTileEntity>> FORGERING_TABLE_TILE_ENTITY = TILE_ENTITY_TYPES
			.register("forgering_table_tile_entity", () -> TileEntityType.Builder
					.create(ForgeringTableTileEntity::new, ModBlocks.FORGERING_TABLE.get()).build(null));
}
