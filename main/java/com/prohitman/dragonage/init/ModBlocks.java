package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.blocks.CandleBlock;
import com.prohitman.dragonage.blocks.SteelBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks 
{
	//Blocks
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, DragonAge.MOD_ID);

	public static final RegistryObject<Block> STEEL_BLOCK = BLOCKS.register("steel_block", () -> new SteelBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F, 6.5F).sound(SoundType.METAL)));

	//Decorations
	public static final RegistryObject<Block> CANDLE = BLOCKS.register("dragonage_candle", () -> new CandleBlock(Block.Properties.create(Material.MISCELLANEOUS).notSolid().hardnessAndResistance(0.0F, 0.0F).lightValue(10).sound(SoundType.SCAFFOLDING)));
	
	
	//Tile Entities
}
